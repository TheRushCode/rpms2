import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import {
  Briefcase,
  Search,
  Plus,
  Eye,
  Edit2,
  Calendar,
  Building2,
  CheckCircle,
  AlertCircle,
  MapPin,
  IndianRupee
} from 'lucide-react';

export const Jobs = () => {
  const [jobs, setJobs] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [activeTab, setActiveTab] = useState('ALL'); // 'ALL' or 'OPEN'
  const [search, setSearch] = useState('');
  const [companyId, setCompanyId] = useState('');
  const [status, setStatus] = useState('');

  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);

  const initialForm = {
    companyId: '',
    jobTitle: '',
    employmentType: 'FULL_TIME',
    location: '',
    openings: 5,
    minPackageLpa: '',
    maxPackageLpa: '',
    minPercentage: 60,
    minPassingYear: 2024,
    maxPassingYear: 2026,
    maxBacklogs: 0,
    experienceMinYears: 0,
    qualificationRule: 'BE/BTech/MCA',
    postedDate: new Date().toISOString().split('T')[0],
    applicationDeadline: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
    status: 'OPEN',
  };
  const [formData, setFormData] = useState(initialForm);
  const [formError, setFormError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchCompanies();
  }, []);

  useEffect(() => {
    fetchJobs();
  }, [page, search, companyId, status, activeTab]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchCompanies = async () => {
    try {
      const res = await api.get('/companies/active');
      if (res.data.success) setCompanies(res.data.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchJobs = async () => {
    setLoading(true);
    try {
      if (activeTab === 'OPEN') {
        const res = await api.get('/jobs/open');
        if (res.data.success) {
          setJobs(res.data.data);
          setTotalPages(1);
          setTotalElements(res.data.data.length);
        }
      } else {
        const res = await api.get('/jobs', {
          params: {
            search: search || undefined,
            companyId: companyId || undefined,
            status: status || undefined,
            page,
            size: 10,
          },
        });
        if (res.data.success) {
          setJobs(res.data.data.content);
          setTotalPages(res.data.data.totalPages);
          setTotalElements(res.data.data.totalElements);
        }
      }
    } catch (err) {
      console.error('Failed to load jobs', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAdd = () => {
    setFormData(initialForm);
    setFormError('');
    setIsAddOpen(true);
  };

  const handleOpenEdit = (j) => {
    setSelectedJob(j);
    setFormData({
      companyId: j.company?.companyId || '',
      jobTitle: j.jobTitle || '',
      employmentType: j.employmentType || 'FULL_TIME',
      location: j.location || '',
      openings: j.openings || 1,
      minPackageLpa: j.minPackageLpa || '',
      maxPackageLpa: j.maxPackageLpa || '',
      minPercentage: j.minPercentage || 0,
      minPassingYear: j.minPassingYear || '',
      maxPassingYear: j.maxPassingYear || '',
      maxBacklogs: j.maxBacklogs ?? 0,
      experienceMinYears: j.experienceMinYears ?? 0,
      qualificationRule: j.qualificationRule || '',
      postedDate: j.postedDate || '',
      applicationDeadline: j.applicationDeadline || '',
      status: j.status || 'OPEN',
    });
    setFormError('');
    setIsEditOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);

    try {
      if (isEditOpen && selectedJob) {
        await api.put(`/jobs/${selectedJob.jobId}`, formData);
        showFeedback('Job opening updated successfully!');
        setIsEditOpen(false);
      } else {
        await api.post('/jobs', formData);
        showFeedback('Job opening published successfully!');
        setIsAddOpen(false);
      }
      fetchJobs();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Operation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleStatusToggle = async (j, newStatus) => {
    try {
      await api.patch(`/jobs/${j.jobId}/status`, { status: newStatus });
      showFeedback(`Job status updated to ${newStatus}`);
      fetchJobs();
    } catch (err) {
      showFeedback('Failed to update status', 'error');
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
            <Briefcase className="w-6 h-6 text-indigo-600" />
            Job Postings &amp; Opportunities
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Create and maintain campus hiring drives, eligibility requirements, and deadlines.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Post New Job
        </button>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-slate-200">
        <button
          onClick={() => { setActiveTab('ALL'); setPage(0); }}
          className={`py-2.5 px-4 text-xs font-semibold border-b-2 transition-all ${
            activeTab === 'ALL'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          All Job Openings ({totalElements})
        </button>
        <button
          onClick={() => { setActiveTab('OPEN'); setPage(0); }}
          className={`py-2.5 px-4 text-xs font-semibold border-b-2 transition-all ${
            activeTab === 'OPEN'
              ? 'border-indigo-600 text-indigo-600'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Open Jobs (Starter App View)
        </button>
      </div>

      {/* Filters (only for ALL tab) */}
      {activeTab === 'ALL' && (
        <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-200/80 flex flex-wrap items-center gap-3">
          <div className="relative flex-1 min-w-[200px]">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
              placeholder="Search by job title, code, company, location..."
              className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <select
            value={companyId}
            onChange={(e) => { setCompanyId(e.target.value); setPage(0); }}
            className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Companies</option>
            {companies.map((c) => (
              <option key={c.companyId} value={c.companyId}>{c.companyName}</option>
            ))}
          </select>

          <select
            value={status}
            onChange={(e) => { setStatus(e.target.value); setPage(0); }}
            className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Statuses</option>
            <option value="OPEN">OPEN</option>
            <option value="CLOSED">CLOSED</option>
            <option value="DRAFT">DRAFT</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>
      )}

      {/* Grid of Job Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {loading ? (
          <div className="col-span-3 py-16 text-center text-slate-400">Loading job openings...</div>
        ) : jobs.length === 0 ? (
          <div className="col-span-3 py-16 text-center text-slate-400">No jobs found matching criteria.</div>
        ) : (
          jobs.map((j) => (
            <div key={j.jobId} className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 hover:shadow-md transition-shadow flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <div>
                    <span className="text-[10px] font-mono font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-200">
                      {j.jobCode}
                    </span>
                    <h3 className="font-bold text-slate-900 text-sm mt-1">{j.jobTitle}</h3>
                    <p className="text-xs text-slate-600 flex items-center gap-1 mt-0.5">
                      <Building2 className="w-3.5 h-3.5 text-slate-400" />
                      {j.company?.companyName}
                    </p>
                  </div>
                  <Badge status={j.status} />
                </div>

                <div className="space-y-1.5 text-xs text-slate-600 border-t border-slate-100 pt-3">
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Location:</span>
                    <span className="font-medium flex items-center gap-1"><MapPin className="w-3 h-3 text-slate-400" /> {j.location}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Openings:</span>
                    <span className="font-semibold text-slate-800">{j.openings} positions</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Package:</span>
                    <span className="font-semibold text-emerald-600">
                      {j.minPackageLpa && j.maxPackageLpa ? `₹${j.minPackageLpa} - ₹${j.maxPackageLpa} LPA` : 'Not Specified'}
                    </span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Min Percentage:</span>
                    <span className="font-medium">{j.minPercentage}%</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Passing Year:</span>
                    <span className="font-medium">{j.minPassingYear} - {j.maxPassingYear}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Max Backlogs:</span>
                    <span className="font-medium text-slate-700">{j.maxBacklogs}</span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Deadline:</span>
                    <span className={`font-semibold flex items-center gap-1 ${new Date() > new Date(j.applicationDeadline) ? 'text-rose-600' : 'text-slate-700'}`}>
                      <Calendar className="w-3 h-3 text-slate-400" /> {j.applicationDeadline}
                    </span>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between pt-4 mt-4 border-t border-slate-100">
                <div className="flex items-center gap-1">
                  {j.status === 'OPEN' ? (
                    <button
                      onClick={() => handleStatusToggle(j, 'CLOSED')}
                      className="text-[11px] px-2 py-1 rounded-lg border border-slate-200 text-slate-600 hover:bg-slate-100"
                    >
                      Close Job
                    </button>
                  ) : (
                    <button
                      onClick={() => handleStatusToggle(j, 'OPEN')}
                      className="text-[11px] px-2 py-1 rounded-lg border border-emerald-200 text-emerald-700 bg-emerald-50 hover:bg-emerald-100"
                    >
                      Open Job
                    </button>
                  )}
                </div>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => { setSelectedJob(j); setIsDetailOpen(true); }}
                    className="p-1.5 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-indigo-50"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleOpenEdit(j)}
                    className="p-1.5 rounded-lg text-slate-500 hover:text-amber-600 hover:bg-amber-50"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add / Edit Job Modal */}
      <Modal
        isOpen={isAddOpen || isEditOpen}
        onClose={() => { setIsAddOpen(false); setIsEditOpen(false); }}
        title={isEditOpen ? 'Edit Job Opening' : 'Publish New Job Opening'}
      >
        <form onSubmit={handleSave} className="space-y-4">
          {formError && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Company *</label>
              <select
                required
                value={formData.companyId}
                onChange={(e) => setFormData({ ...formData, companyId: parseInt(e.target.value) || '' })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">Select Company</option>
                {companies.map((c) => (
                  <option key={c.companyId} value={c.companyId}>{c.companyName}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Job Title *</label>
              <input
                type="text"
                required
                value={formData.jobTitle}
                onChange={(e) => setFormData({ ...formData, jobTitle: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Location *</label>
              <input
                type="text"
                required
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Openings *</label>
              <input
                type="number"
                required
                min="1"
                value={formData.openings}
                onChange={(e) => setFormData({ ...formData, openings: parseInt(e.target.value) || 1 })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Min Package (LPA)</label>
              <input
                type="number"
                step="0.1"
                value={formData.minPackageLpa}
                onChange={(e) => setFormData({ ...formData, minPackageLpa: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Max Package (LPA)</label>
              <input
                type="number"
                step="0.1"
                value={formData.maxPackageLpa}
                onChange={(e) => setFormData({ ...formData, maxPackageLpa: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Min Percentage Required (%)</label>
              <input
                type="number"
                step="0.1"
                min="0"
                max="100"
                value={formData.minPercentage}
                onChange={(e) => setFormData({ ...formData, minPercentage: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Max Backlogs Allowed</label>
              <input
                type="number"
                min="0"
                value={formData.maxBacklogs}
                onChange={(e) => setFormData({ ...formData, maxBacklogs: parseInt(e.target.value) || 0 })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Passing Year (Min)</label>
              <input
                type="number"
                value={formData.minPassingYear}
                onChange={(e) => setFormData({ ...formData, minPassingYear: parseInt(e.target.value) || '' })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Passing Year (Max)</label>
              <input
                type="number"
                value={formData.maxPassingYear}
                onChange={(e) => setFormData({ ...formData, maxPassingYear: parseInt(e.target.value) || '' })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Posted Date *</label>
              <input
                type="date"
                required
                value={formData.postedDate}
                onChange={(e) => setFormData({ ...formData, postedDate: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Application Deadline *</label>
              <input
                type="date"
                required
                value={formData.applicationDeadline}
                onChange={(e) => setFormData({ ...formData, applicationDeadline: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => { setIsAddOpen(false); setIsEditOpen(false); }}
              className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-600/30"
            >
              {actionLoading ? 'Saving...' : 'Save Job Opening'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Detail Modal */}
      {selectedJob && (
        <Modal isOpen={isDetailOpen} onClose={() => setIsDetailOpen(false)} title={`Job Details: ${selectedJob.jobCode}`}>
          <div className="space-y-4 text-xs">
            <div className="grid grid-cols-2 gap-3 p-4 bg-slate-50 rounded-xl">
              <div><span className="text-slate-400 block">Job Title:</span> <strong className="text-slate-800 text-sm">{selectedJob.jobTitle}</strong></div>
              <div><span className="text-slate-400 block">Company:</span> <strong className="text-slate-800">{selectedJob.company?.companyName}</strong></div>
              <div><span className="text-slate-400 block">Location:</span> {selectedJob.location}</div>
              <div><span className="text-slate-400 block">Openings:</span> {selectedJob.openings} vacancies</div>
              <div><span className="text-slate-400 block">Package Range:</span> ₹{selectedJob.minPackageLpa} - ₹{selectedJob.maxPackageLpa} LPA</div>
              <div><span className="text-slate-400 block">Min Academic Score:</span> {selectedJob.minPercentage}%</div>
              <div><span className="text-slate-400 block">Passing Years Allowed:</span> {selectedJob.minPassingYear} to {selectedJob.maxPassingYear}</div>
              <div><span className="text-slate-400 block">Max Backlogs Allowed:</span> {selectedJob.maxBacklogs}</div>
              <div><span className="text-slate-400 block">Qualification Filter:</span> {selectedJob.qualificationRule || 'All'}</div>
              <div><span className="text-slate-400 block">Status:</span> <Badge status={selectedJob.status} /></div>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
