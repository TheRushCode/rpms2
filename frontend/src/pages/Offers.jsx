import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import {
  Award,
  Plus,
  CheckCircle,
  AlertCircle,
  IndianRupee,
  Building2,
  Calendar
} from 'lucide-react';

export const Offers = () => {
  const [offers, setOffers] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  const [isAddOpen, setIsAddOpen] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  const [formData, setFormData] = useState({
    applicationId: '',
    designation: 'Associate Software Engineer',
    offeredPackageLpa: 4.5,
    offerDate: new Date().toISOString().split('T')[0],
    expectedJoiningDate: new Date(Date.now() + 21 * 86400000).toISOString().split('T')[0],
    offerStatus: 'OFFERED',
    remarks: 'Formal campus placement offer letter issued.',
  });

  useEffect(() => {
    fetchApplications();
    fetchOffers();
  }, [page]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchApplications = async () => {
    try {
      const res = await api.get('/applications', { params: { size: 100 } });
      if (res.data.success) {
        // Filter applications that are SELECTED or INTERVIEWED
        setApplications(res.data.data.content);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const fetchOffers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/offers', { params: { page, size: 10 } });
      if (res.data.success) setOffers(res.data.data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOffer = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);
    try {
      await api.post('/offers', formData);
      showFeedback('Offer issued successfully!');
      setIsAddOpen(false);
      fetchOffers();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Offer creation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdateStatus = async (offerId, status) => {
    try {
      await api.patch(`/offers/${offerId}/status`, { status });
      showFeedback(`Offer status updated to ${status}`);
      fetchOffers();
    } catch (err) {
      showFeedback('Failed to update offer status', 'error');
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
            <Award className="w-6 h-6 text-indigo-600" />
            Job Offers &amp; Packages
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Issue and track formal employment offers for shortlisted candidates.
          </p>
        </div>
        <button
          onClick={() => { setIsAddOpen(true); setFormError(''); }}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Generate Offer
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3 px-4">Offer Code</th>
                <th className="py-3 px-4">Candidate</th>
                <th className="py-3 px-4">Company &amp; Job</th>
                <th className="py-3 px-4">Designation</th>
                <th className="py-3 px-4">Package</th>
                <th className="py-3 px-4">Joining Date</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400">Loading offers...</td>
                </tr>
              ) : offers.length === 0 ? (
                <tr>
                  <td colSpan="8" className="py-8 text-center text-slate-400">No offers found.</td>
                </tr>
              ) : (
                offers.map((o) => (
                  <tr key={o.offerId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4 font-mono font-bold text-indigo-700">{o.offerCode}</td>
                    <td className="py-3 px-4 font-semibold text-slate-800">
                      {o.application?.candidate?.fullName}
                      <span className="text-[11px] text-slate-400 block">{o.application?.candidate?.candidateCode}</span>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      <span className="font-medium text-slate-800 block">{o.application?.job?.company?.companyName}</span>
                      <span className="text-[11px] text-slate-400">{o.application?.job?.jobTitle}</span>
                    </td>
                    <td className="py-3 px-4 font-medium text-slate-700">{o.designation}</td>
                    <td className="py-3 px-4 font-bold text-emerald-600">₹{o.offeredPackageLpa} LPA</td>
                    <td className="py-3 px-4 text-slate-600">{o.expectedJoiningDate || 'TBD'}</td>
                    <td className="py-3 px-4">
                      <Badge status={o.offerStatus} />
                    </td>
                    <td className="py-3 px-4 text-right space-x-1">
                      {o.offerStatus === 'OFFERED' && (
                        <>
                          <button
                            onClick={() => handleUpdateStatus(o.offerId, 'ACCEPTED')}
                            className="px-2 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 font-semibold rounded-lg text-[11px] border border-emerald-200"
                          >
                            Accept
                          </button>
                          <button
                            onClick={() => handleUpdateStatus(o.offerId, 'REJECTED')}
                            className="px-2 py-1 bg-rose-50 hover:bg-rose-100 text-rose-700 font-semibold rounded-lg text-[11px] border border-rose-200"
                          >
                            Reject
                          </button>
                        </>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal */}
      <Modal isOpen={isAddOpen} onClose={() => setIsAddOpen(false)} title="Generate Campus Offer Letter">
        <form onSubmit={handleCreateOffer} className="space-y-4">
          {formError && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Application *</label>
            <select
              required
              value={formData.applicationId}
              onChange={(e) => setFormData({ ...formData, applicationId: parseInt(e.target.value) || '' })}
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">-- Choose Candidate Application --</option>
              {applications.map((app) => (
                <option key={app.applicationId} value={app.applicationId}>
                  {app.applicationCode} &bull; {app.candidate?.fullName} &bull; {app.job?.jobTitle} @ {app.job?.company?.companyName}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Designation *</label>
              <input
                type="text"
                required
                value={formData.designation}
                onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Offered Package (LPA) *</label>
              <input
                type="number"
                step="0.1"
                required
                min="0"
                value={formData.offeredPackageLpa}
                onChange={(e) => setFormData({ ...formData, offeredPackageLpa: parseFloat(e.target.value) || 0 })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Offer Date *</label>
              <input
                type="date"
                required
                value={formData.offerDate}
                onChange={(e) => setFormData({ ...formData, offerDate: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Expected Joining Date</label>
              <input
                type="date"
                value={formData.expectedJoiningDate}
                onChange={(e) => setFormData({ ...formData, expectedJoiningDate: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsAddOpen(false)}
              className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-600/30"
            >
              {actionLoading ? 'Creating...' : 'Issue Offer'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
