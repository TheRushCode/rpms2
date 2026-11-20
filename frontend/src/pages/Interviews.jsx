import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import {
  CalendarCheck,
  Plus,
  Video,
  MapPin,
  CheckCircle,
  AlertCircle,
  Star,
  MessageSquare
} from 'lucide-react';

export const Interviews = () => {
  const [interviews, setInterviews] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [isScheduleOpen, setIsScheduleOpen] = useState(false);
  const [isFeedbackOpen, setIsFeedbackOpen] = useState(false);
  const [selectedInterview, setSelectedInterview] = useState(null);

  // Form states
  const [scheduleForm, setScheduleForm] = useState({
    applicationId: '',
    roundNumber: 1,
    roundType: 'TECHNICAL',
    scheduledDate: new Date(Date.now() + 86400000).toISOString().split('T')[0],
    scheduledTime: '11:00:00',
    mode: 'ONLINE',
    interviewerName: '',
    meetingOrLocation: '',
  });

  const [feedbackForm, setFeedbackForm] = useState({
    technicalRating: 4.0,
    communicationRating: 4.0,
    problemSolvingRating: 4.0,
    overallRating: 4.0,
    recommendation: 'NEXT_ROUND',
    remarks: 'Good fundamentals demonstrated.',
  });

  const [formError, setFormError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchApplications();
    fetchInterviews();
  }, [page]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchApplications = async () => {
    try {
      const res = await api.get('/applications', { params: { size: 100 } });
      if (res.data.success) setApplications(res.data.data.content);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchInterviews = async () => {
    setLoading(true);
    try {
      const res = await api.get('/interviews', { params: { page, size: 10 } });
      if (res.data.success) {
        setInterviews(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSchedule = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);
    try {
      await api.post('/interviews', scheduleForm);
      showFeedback('Interview round scheduled successfully!');
      setIsScheduleOpen(false);
      fetchInterviews();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Scheduling failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleSubmitFeedback = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    try {
      await api.post(`/interviews/${selectedInterview.interviewId}/feedback`, feedbackForm);
      showFeedback('Interview evaluation submitted!');
      setIsFeedbackOpen(false);
      fetchInterviews();
    } catch (err) {
      showFeedback(err.response?.data?.message || 'Feedback submission failed', 'error');
    } finally {
      setActionLoading(false);
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
            <CalendarCheck className="w-6 h-6 text-indigo-600" />
            Interview Rounds &amp; Evaluations
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Coordinate Technical, HR, and Managerial rounds with candidate performance scoring.
          </p>
        </div>
        <button
          onClick={() => { setIsScheduleOpen(true); setFormError(''); }}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Schedule Interview
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3 px-4">Round</th>
                <th className="py-3 px-4">Candidate</th>
                <th className="py-3 px-4">Job &amp; Company</th>
                <th className="py-3 px-4">Scheduled Date &amp; Time</th>
                <th className="py-3 px-4">Mode / Location</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">Loading interview rounds...</td>
                </tr>
              ) : interviews.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">No scheduled interview rounds.</td>
                </tr>
              ) : (
                interviews.map((iv) => (
                  <tr key={iv.interviewId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4">
                      <span className="font-bold text-slate-900 block">Round {iv.roundNumber}</span>
                      <span className="text-[10px] font-semibold text-indigo-600 uppercase">{iv.roundType}</span>
                    </td>
                    <td className="py-3 px-4 font-semibold text-slate-800">
                      {iv.application?.candidate?.fullName}
                      <span className="text-[11px] text-slate-400 block">{iv.application?.candidate?.candidateCode}</span>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      <span className="font-medium text-slate-800 block">{iv.application?.job?.jobTitle}</span>
                      <span className="text-[11px] text-slate-400">{iv.application?.job?.company?.companyName}</span>
                    </td>
                    <td className="py-3 px-4 text-slate-700">
                      <div className="font-medium">{iv.scheduledDate}</div>
                      <div className="text-[11px] text-slate-400">{iv.scheduledTime || 'TBD'}</div>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      <span className="inline-flex items-center gap-1 font-medium">
                        {iv.mode === 'ONLINE' ? <Video className="w-3.5 h-3.5 text-indigo-500" /> : <MapPin className="w-3.5 h-3.5 text-slate-400" />}
                        {iv.mode}
                      </span>
                      {iv.meetingOrLocation && <div className="text-[11px] text-slate-400 truncate max-w-xs">{iv.meetingOrLocation}</div>}
                    </td>
                    <td className="py-3 px-4">
                      <Badge status={iv.status} />
                    </td>
                    <td className="py-3 px-4 text-right">
                      {iv.status !== 'COMPLETED' ? (
                        <button
                          onClick={() => { setSelectedInterview(iv); setIsFeedbackOpen(true); }}
                          className="px-2.5 py-1 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-semibold rounded-lg text-[11px] border border-indigo-200"
                        >
                          Submit Feedback
                        </button>
                      ) : (
                        <span className="text-[11px] text-emerald-600 font-semibold flex items-center justify-end gap-1">
                          <CheckCircle className="w-3.5 h-3.5" /> Evaluated
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Schedule Modal */}
      <Modal isOpen={isScheduleOpen} onClose={() => setIsScheduleOpen(false)} title="Schedule Interview Round">
        <form onSubmit={handleSchedule} className="space-y-4">
          {formError && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Select Application *</label>
            <select
              required
              value={scheduleForm.applicationId}
              onChange={(e) => setScheduleForm({ ...scheduleForm, applicationId: parseInt(e.target.value) || '' })}
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">-- Choose Candidate Application --</option>
              {applications.map((app) => (
                <option key={app.applicationId} value={app.applicationId}>
                  {app.applicationCode} &bull; {app.candidate?.fullName} &bull; {app.job?.jobTitle} ({app.applicationStatus})
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Round Number *</label>
              <input
                type="number"
                required
                min="1"
                value={scheduleForm.roundNumber}
                onChange={(e) => setScheduleForm({ ...scheduleForm, roundNumber: parseInt(e.target.value) || 1 })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Round Type *</label>
              <select
                value={scheduleForm.roundType}
                onChange={(e) => setScheduleForm({ ...scheduleForm, roundType: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                <option value="TECHNICAL">TECHNICAL</option>
                <option value="HR">HR</option>
                <option value="MANAGERIAL">MANAGERIAL</option>
                <option value="ONLINE_TEST">ONLINE_TEST</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Scheduled Date *</label>
              <input
                type="date"
                required
                value={scheduleForm.scheduledDate}
                onChange={(e) => setScheduleForm({ ...scheduleForm, scheduledDate: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Scheduled Time</label>
              <input
                type="time"
                value={scheduleForm.scheduledTime}
                onChange={(e) => setScheduleForm({ ...scheduleForm, scheduledTime: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Mode *</label>
              <select
                value={scheduleForm.mode}
                onChange={(e) => setScheduleForm({ ...scheduleForm, mode: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                <option value="ONLINE">ONLINE</option>
                <option value="OFFLINE">OFFLINE</option>
                <option value="TELEPHONIC">TELEPHONIC</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Interviewer Name</label>
              <input
                type="text"
                value={scheduleForm.interviewerName}
                onChange={(e) => setScheduleForm({ ...scheduleForm, interviewerName: e.target.value })}
                placeholder="e.g. Lead Architect"
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Meeting Link or Room Location</label>
            <input
              type="text"
              value={scheduleForm.meetingOrLocation}
              onChange={(e) => setScheduleForm({ ...scheduleForm, meetingOrLocation: e.target.value })}
              placeholder="https://meet.google.com/... or Conference Hall 2"
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsScheduleOpen(false)}
              className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-600/30"
            >
              {actionLoading ? 'Scheduling...' : 'Schedule Round'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Feedback Modal */}
      {selectedInterview && (
        <Modal
          isOpen={isFeedbackOpen}
          onClose={() => setIsFeedbackOpen(false)}
          title={`Evaluate Round ${selectedInterview.roundNumber}: ${selectedInterview.application?.candidate?.fullName}`}
        >
          <form onSubmit={handleSubmitFeedback} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Technical Rating (0-5)</label>
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  max="5"
                  value={feedbackForm.technicalRating}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, technicalRating: parseFloat(e.target.value) || 0 })}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Communication Rating (0-5)</label>
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  max="5"
                  value={feedbackForm.communicationRating}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, communicationRating: parseFloat(e.target.value) || 0 })}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Problem Solving Rating (0-5)</label>
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  max="5"
                  value={feedbackForm.problemSolvingRating}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, problemSolvingRating: parseFloat(e.target.value) || 0 })}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Recommendation *</label>
                <select
                  required
                  value={feedbackForm.recommendation}
                  onChange={(e) => setFeedbackForm({ ...feedbackForm, recommendation: e.target.value })}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="SELECTED">SELECTED (Ready for Offer)</option>
                  <option value="NEXT_ROUND">NEXT_ROUND (Schedule Follow-up)</option>
                  <option value="ON_HOLD">ON_HOLD</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Remarks &amp; Feedback Notes</label>
              <textarea
                rows="3"
                value={feedbackForm.remarks}
                onChange={(e) => setFeedbackForm({ ...feedbackForm, remarks: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setIsFeedbackOpen(false)}
                className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={actionLoading}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {actionLoading ? 'Saving...' : 'Submit Evaluation'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
