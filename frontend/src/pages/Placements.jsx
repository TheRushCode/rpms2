import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import {
  CheckCircle2,
  Plus,
  Search,
  Building2,
  CheckCircle,
  AlertCircle,
  UserCheck,
  Calendar
} from 'lucide-react';

export const Placements = () => {
  const [placements, setPlacements] = useState([]);
  const [offers, setOffers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  const [isRecordOpen, setIsRecordOpen] = useState(false);
  const [isUpdateOpen, setIsUpdateOpen] = useState(false);
  const [selectedPlacement, setSelectedPlacement] = useState(null);

  const [recordOfferId, setRecordOfferId] = useState('');
  const [joiningStatus, setJoiningStatus] = useState('JOINED');
  const [actualJoiningDate, setActualJoiningDate] = useState(new Date().toISOString().split('T')[0]);

  const [actionLoading, setActionLoading] = useState(false);
  const [formError, setFormError] = useState('');
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchOffers();
    fetchPlacements();
  }, [page]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchOffers = async () => {
    try {
      const res = await api.get('/offers', { params: { size: 100 } });
      if (res.data.success) {
        setOffers(res.data.data.content);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const fetchPlacements = async () => {
    setLoading(true);
    try {
      const res = await api.get('/placements', { params: { page, size: 10 } });
      if (res.data.success) {
        setPlacements(res.data.data.content);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRecordPlacement = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);
    try {
      await api.post('/placements', {
        offerId: parseInt(recordOfferId),
        joiningStatus: 'JOINED',
        actualJoiningDate: new Date().toISOString().split('T')[0],
      });
      showFeedback('Placement recorded! Candidate status updated to PLACED.');
      setIsRecordOpen(false);
      setRecordOfferId('');
      fetchPlacements();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Operation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdateJoiningStatus = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    try {
      await api.patch(`/placements/${selectedPlacement.placementId}/status`, {
        joiningStatus,
        actualJoiningDate: joiningStatus === 'JOINED' ? actualJoiningDate : undefined,
      });
      showFeedback(`Joining status updated to ${joiningStatus}`);
      setIsUpdateOpen(false);
      fetchPlacements();
    } catch (err) {
      showFeedback('Failed to update joining status', 'error');
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
            <CheckCircle2 className="w-6 h-6 text-indigo-600" />
            Confirmed Placements &amp; Joinings
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Verified campus placements, compensation records, and candidate joining status.
          </p>
        </div>
        <button
          onClick={() => { setIsRecordOpen(true); setFormError(''); }}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Record Final Placement
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3 px-4">Candidate</th>
                <th className="py-3 px-4">Company</th>
                <th className="py-3 px-4">Job Title</th>
                <th className="py-3 px-4">Final Package</th>
                <th className="py-3 px-4">Joining Date</th>
                <th className="py-3 px-4">Joining Status</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">Loading placement records...</td>
                </tr>
              ) : placements.length === 0 ? (
                <tr>
                  <td colSpan="7" className="py-8 text-center text-slate-400">No placements recorded yet.</td>
                </tr>
              ) : (
                placements.map((p) => (
                  <tr key={p.placementId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4 font-semibold text-slate-800">
                      {p.candidate?.fullName}
                      <span className="text-[11px] text-slate-400 block">{p.candidate?.candidateCode}</span>
                    </td>
                    <td className="py-3 px-4 text-slate-700 font-medium">{p.company?.companyName}</td>
                    <td className="py-3 px-4 text-slate-600">{p.job?.jobTitle}</td>
                    <td className="py-3 px-4 font-bold text-emerald-600">₹{p.finalPackageLpa} LPA</td>
                    <td className="py-3 px-4 text-slate-600">
                      {p.actualJoiningDate || p.expectedJoiningDate || 'Pending'}
                    </td>
                    <td className="py-3 px-4">
                      <Badge status={p.joiningStatus} />
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => {
                          setSelectedPlacement(p);
                          setJoiningStatus(p.joiningStatus);
                          setActualJoiningDate(p.actualJoiningDate || new Date().toISOString().split('T')[0]);
                          setIsUpdateOpen(true);
                        }}
                        className="px-2.5 py-1 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 font-semibold rounded-lg text-[11px] border border-indigo-200"
                      >
                        Update Joining
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Record Modal */}
      <Modal isOpen={isRecordOpen} onClose={() => setIsRecordOpen(false)} title="Record Placement From Offer">
        <form onSubmit={handleRecordPlacement} className="space-y-4">
          {formError && (
            <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{formError}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Select Offer *</label>
            <select
              required
              value={recordOfferId}
              onChange={(e) => setRecordOfferId(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">-- Choose Candidate Offer --</option>
              {offers.map((o) => (
                <option key={o.offerId} value={o.offerId}>
                  {o.offerCode} &bull; {o.application?.candidate?.fullName} &bull; {o.application?.job?.company?.companyName} (₹{o.offeredPackageLpa} LPA) [{o.offerStatus}]
                </option>
              ))}
            </select>
          </div>

          <p className="text-xs text-slate-500 bg-slate-50 p-3 rounded-xl border border-slate-200">
            Note: Recording placement marks the candidate's profile status as <strong className="text-emerald-700">PLACED</strong> and finalizes recruitment.
          </p>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => setIsRecordOpen(false)}
              className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={actionLoading}
              className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-md shadow-indigo-600/30"
            >
              {actionLoading ? 'Recording...' : 'Record Placement'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Update Joining Modal */}
      {selectedPlacement && (
        <Modal isOpen={isUpdateOpen} onClose={() => setIsUpdateOpen(false)} title="Update Joining Status" maxWidth="max-w-md">
          <form onSubmit={handleUpdateJoiningStatus} className="space-y-4">
            <div>
              <p className="text-xs text-slate-500 mb-2">
                Candidate: <strong className="text-slate-800">{selectedPlacement.candidate?.fullName}</strong>
              </p>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Joining Status *</label>
              <select
                required
                value={joiningStatus}
                onChange={(e) => setJoiningStatus(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                <option value="JOINED">JOINED</option>
                <option value="PENDING">PENDING</option>
                <option value="NOT_JOINED">NOT_JOINED</option>
                <option value="DEFERRED">DEFERRED</option>
                <option value="NO_SHOW">NO_SHOW</option>
              </select>
            </div>

            {joiningStatus === 'JOINED' && (
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Actual Joining Date</label>
                <input
                  type="date"
                  value={actualJoiningDate}
                  onChange={(e) => setActualJoiningDate(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            )}

            <div className="flex justify-end gap-3 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setIsUpdateOpen(false)}
                className="px-4 py-2 rounded-xl text-xs font-medium text-slate-600 bg-slate-100 hover:bg-slate-200"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={actionLoading}
                className="px-4 py-2 rounded-xl text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {actionLoading ? 'Updating...' : 'Save Status'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};
