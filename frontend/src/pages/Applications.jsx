import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import {
  FileText,
  Search,
  Plus,
  History,
  CheckCircle,
  XCircle,
  AlertCircle,
  ArrowRight,
  ShieldCheck,
  Building2,
  Calendar
} from 'lucide-react';

export const Applications = () => {
  const [applications, setApplications] = useState([]);
  const [candidates, setCandidates] = useState([]);
  const [openJobs, setOpenJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filters
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [eligibility, setEligibility] = useState('');

  // Modals
  const [isApplyOpen, setIsApplyOpen] = useState(false);
  const [isStatusOpen, setIsStatusOpen] = useState(false);
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [selectedApp, setSelectedApp] = useState(null);
  const [appHistory, setAppHistory] = useState([]);

  // Apply Form
  const [applyCandidateId, setApplyCandidateId] = useState('');
  const [applyJobId, setApplyJobId] = useState('');
  const [eligibilityCheck, setEligibilityCheck] = useState(null);
  const [eligibilityLoading, setEligibilityLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [formError, setFormError] = useState('');

  // Status Form
  const [newStatus, setNewStatus] = useState('');
  const [statusRemarks, setStatusRemarks] = useState('');

  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  const workflowStatuses = [
    'APPLIED',
    'ELIGIBILITY_VERIFIED',
    'SHORTLISTED',
    'INTERVIEW_SCHEDULED',
    'INTERVIEWED',
    'SELECTED',
    'OFFERED',
    'JOINED',
    'REJECTED',
    'WITHDRAWN',
  ];

  useEffect(() => {
    fetchCandidatesAndJobs();
  }, []);

  useEffect(() => {
    fetchApplications();
  }, [page, search, status, eligibility]);

  // Live eligibility check when candidate and job are selected
  useEffect(() => {
    if (applyCandidateId && applyJobId) {
      checkLiveEligibility(applyCandidateId, applyJobId);
    } else {
      setEligibilityCheck(null);
    }
  }, [applyCandidateId, applyJobId]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchCandidatesAndJobs = async () => {
    try {
      const [candRes, jobRes] = await Promise.all([
        api.get('/candidates/all'),
        api.get('/jobs/open')
      ]);
      if (candRes.data.success) setCandidates(candRes.data.data);
      if (jobRes.data.success) setOpenJobs(jobRes.data.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchApplications = async () => {
    setLoading(true);
    try {
      const res = await api.get('/applications', {
        params: {
          search: search || undefined,
          status: status || undefined,
          eligibility: eligibility || undefined,
          page,
          size: 10,
        },
      });
      if (res.data.success) {
        setApplications(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setTotalElements(res.data.data.totalElements);
      }
    } catch (err) {
      console.error('Failed to load applications', err);
    } finally {
      setLoading(false);
    }
  };

  const checkLiveEligibility = async (cId, jId) => {
    setEligibilityLoading(true);
    try {
      const res = await api.get('/applications/check-eligibility', {
        params: { candidateId: cId, jobId: jId },
      });
      if (res.data.success) {
        setEligibilityCheck(res.data.data);
      }
    } catch (e) {
      setEligibilityCheck(null);
    } finally {
      setEligibilityLoading(false);
    }
  };

  const handleApply = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);
    try {
      await api.post('/applications', {
        candidateId: parseInt(applyCandidateId),
        jobId: parseInt(applyJobId),
      });
      showFeedback('Application submitted successfully!');
      setIsApplyOpen(false);
      setApplyCandidateId('');
      setApplyJobId('');
      setEligibilityCheck(null);
      fetchApplications();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Application rejected');
    } finally {
      setActionLoading(false);
    }
  };

  const handleOpenStatus = (app) => {
    setSelectedApp(app);
    setNewStatus(app.applicationStatus);
    setStatusRemarks('');
    setIsStatusOpen(true);
  };

  const handleUpdateStatus = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    try {
      await api.patch(`/applications/${selectedApp.applicationId}/status`, {
        newStatus,
        remarks: statusRemarks,
      });
      showFeedback(`Status updated to ${newStatus}`);
      setIsStatusOpen(false);
      fetchApplications();
    } catch (err) {
      showFeedback(err.response?.data?.message || 'Failed to update status', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleOpenHistory = async (app) => {
    setSelectedApp(app);
    try {
      const res = await api.get(`/applications/${app.applicationId}/history`);
      if (res.data.success) {
        setAppHistory(res.data.data);
        setIsHistoryOpen(true);
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="space-y-6">
      {feedbackMsg.text && (
        <div className={`flex items-center gap-2 p-4 rounded-xl border text-sm font-medium ${feedbackMsg.type === 'error' ? 'bg-rose-50 text-rose-700 border-rose-200' : 'bg-emerald-50 text-emerald-700 border-emerald-200'}`}>
          {feedbackMsg.type === 'error' ? <AlertCircle className="w-5 h-5" /> : <CheckCircle className="w-5 h-5" />}
          <span>{feedbackMsg.text}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <FileText className="w-6 h-6 text-indigo-600" />
            Hiring Applications &amp; Workflow
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Process candidate applications, verify eligibility criteria, and track stage-by-stage hiring progress.
          </p>
        </div>
        <button
          onClick={() => { setIsApplyOpen(true); setFormError(''); }}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Apply Candidate for Job
        </button>
      </div>

      {/* Filter Bar */}
      <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-200/80 flex flex-wrap items-center gap-3">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            placeholder="Search by code, candidate, job, company..."
            className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Workflow Statuses</option>
          {workflowStatuses.map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>

        <select
          value={eligibility}
          onChange={(e) => { setEligibility(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Eligibility States</option>
          <option value="ELIGIBLE">ELIGIBLE</option>
          <option value="NOT_ELIGIBLE">NOT_ELIGIBLE</option>
          <option value="PENDING">PENDING</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3 px-4">Code</th>
                <th className="py-3 px-4">Candidate</th>
                <th className="py-3 px-4">Job &amp; Company</th>
                <th className="py-3 px-4">Applied At</th>
                <th className="py-3 px-4">Eligibility</th>
                <th className="py-3 px-4">Current Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">Loading applications...</td>
                </tr>
              ) : applications.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">No applications found.</td>
                </tr>
              ) : (
                applications.map((app) => (
                  <tr key={app.applicationId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4 font-mono font-bold text-indigo-700">{app.applicationCode}</td>
                    <td className="py-3 px-4">
                      <strong className="text-slate-800 block">{app.candidate?.fullName}</strong>
                      <span className="text-[11px] text-slate-400">{app.candidate?.candidateCode} &bull; {app.candidate?.primaryTechnology}</span>
                    </td>
                    <td className="py-3 px-4">
                      <span className="font-semibold text-slate-800 block">{app.job?.jobTitle}</span>
                      <span className="text-[11px] text-slate-500">{app.job?.company?.companyName}</span>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      {new Date(app.appliedAt).toLocaleDateString()}
                    </td>
                    <td className="py-3 px-4">
                      <Badge status={app.eligibilityStatus} />
                    </td>
                    <td className="py-3 px-4">
                      <Badge status={app.applicationStatus} />
                    </td>
                    <td className="py-3 px-4 text-right space-x-1">
                      <button
                        onClick={() => handleOpenStatus(app)}
                        className="px-2 py-1 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-semibold rounded-lg text-[11px] border border-indigo-200"
                      >
                        Change Status
                      </button>
                      <button
                        onClick={() => handleOpenHistory(app)}
                        title="Audit History"
                        className="p-1 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100"
                      >
                        <History className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between p-4 border-t border-slate-100 text-xs text-slate-500">
          <span>Showing {applications.length} of {totalElements} application records</span>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(p - 1, 0))}
              disabled={page === 0}
              className="px-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40"
            >
              Previous
            </button>
            <span className="font-semibold text-slate-700">Page {page + 1} of {Math.max(totalPages, 1)}</span>
            <button
              onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
              disabled={page >= totalPages - 1}
              className="px-3 py-1.5 rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-40"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {/* Apply for Job Modal with Real-time Eligibility Rules Engine */}
      <Modal
        isOpen={isApplyOpen}
        onClose={() => setIsApplyOpen(false)}
        title="Apply Candidate to Job (Eligibility Enforced)"
      >
        <form onSubmit={handleApply} className="space-y-4">
          {formError && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Select Candidate *</label>
            <select
              required
              value={applyCandidateId}
              onChange={(e) => setApplyCandidateId(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">-- Choose Candidate --</option>
              {candidates.map((c) => (
                <option key={c.candidateId} value={c.candidateId}>
                  {c.candidateCode} - {c.fullName} ({c.primaryTechnology}, {c.percentage}%, {c.backlogCount} backlogs) [{c.profileStatus}]
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Select Open Job *</label>
            <select
              required
              value={applyJobId}
              onChange={(e) => setApplyJobId(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">-- Choose Open Job --</option>
              {openJobs.map((j) => (
                <option key={j.jobId} value={j.jobId}>
                  {j.jobCode} - {j.jobTitle} @ {j.company?.companyName} (Min {j.minPercentage}%, Max {j.maxBacklogs} backlogs)
                </option>
              ))}
            </select>
          </div>

          {/* Live Eligibility Validation Box */}
          {eligibilityLoading && (
            <div className="p-3 bg-slate-50 rounded-xl text-xs text-slate-500 text-center">
              Evaluating eligibility criteria...
            </div>
          )}

          {eligibilityCheck && (
            <div className={`p-4 rounded-xl border text-xs space-y-2 ${
              eligibilityCheck.eligible
                ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                : 'bg-rose-50 border-rose-200 text-rose-800'
            }`}>
              <div className="flex items-center gap-2 font-bold text-sm">
                {eligibilityCheck.eligible ? (
                  <>
                    <CheckCircle className="w-5 h-5 text-emerald-600" />
                    <span>Eligible to Apply</span>
                  </>
                ) : (
                  <>
                    <XCircle className="w-5 h-5 text-rose-600" />
                    <span>Not Eligible to Apply</span>
                  </>
                )}
              </div>

              {eligibilityCheck.alreadyApplied && (
                <p className="font-semibold text-rose-700">
                  &bull; Duplicate application detected: This candidate has already applied to this job.
                </p>
              )}

              {eligibilityCheck.reasons?.length > 0 && (
                <ul className="list-disc pl-5 space-y-1">
                  {eligibilityCheck.reasons.map((r, idx) => (
                    <li key={idx}>{r}</li>
                  ))}
                </ul>
              )}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsApplyOpen(false)}
              className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading || (eligibilityCheck && !eligibilityCheck.eligible)}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-600/30 disabled:opacity-40"
            >
              {actionLoading ? 'Submitting...' : 'Submit Application'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Change Status Modal */}
      {selectedApp && (
        <Modal
          isOpen={isStatusOpen}
          onClose={() => setIsStatusOpen(false)}
          title={`Update Status: ${selectedApp.applicationCode}`}
          maxWidth="max-w-md"
        >
          <form onSubmit={handleUpdateStatus} className="space-y-4">
            <div>
              <p className="text-xs text-slate-500 mb-2">
                Candidate: <strong className="text-slate-800">{selectedApp.candidate?.fullName}</strong> &bull; Job: <strong className="text-slate-800">{selectedApp.job?.jobTitle}</strong>
              </p>
              <label className="block text-xs font-semibold text-slate-700 mb-1">New Workflow Status *</label>
              <select
                required
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                {workflowStatuses.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Remarks / Note</label>
              <textarea
                rows="3"
                value={statusRemarks}
                onChange={(e) => setStatusRemarks(e.target.value)}
                placeholder="Optional remarks regarding this transition..."
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setIsStatusOpen(false)}
                className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={actionLoading}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {actionLoading ? 'Updating...' : 'Update Status'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* History Drawer Modal */}
      {selectedApp && (
        <Modal
          isOpen={isHistoryOpen}
          onClose={() => setIsHistoryOpen(false)}
          title={`Audit History: ${selectedApp.applicationCode}`}
        >
          <div className="space-y-4">
            {appHistory.length === 0 ? (
              <p className="text-xs text-slate-400 py-4 text-center">No history records found.</p>
            ) : (
              <div className="relative pl-6 space-y-6 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-200">
                {appHistory.map((h) => (
                  <div key={h.historyId} className="relative">
                    <div className="absolute -left-6 top-1 w-3 h-3 rounded-full bg-indigo-600 border-2 border-white ring-2 ring-indigo-200" />
                    <div className="p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs space-y-1">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-slate-800">
                          {h.oldStatus ? `${h.oldStatus} → ${h.newStatus}` : `Status: ${h.newStatus}`}
                        </span>
                        <span className="text-[11px] text-slate-400">
                          {new Date(h.changedAt).toLocaleString()}
                        </span>
                      </div>
                      {h.remarks && <p className="text-slate-600">{h.remarks}</p>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
};
