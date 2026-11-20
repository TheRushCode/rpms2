import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import {
  Users,
  Search,
  Plus,
  Eye,
  Edit2,
  Trash2,
  PowerOff,
  Filter,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

export const Candidates = () => {
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filters
  const [search, setSearch] = useState('');
  const [technology, setTechnology] = useState('');
  const [profileStatus, setProfileStatus] = useState('');
  const [placementStatus, setPlacementStatus] = useState('');

  // Modals
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [selectedCandidate, setSelectedCandidate] = useState(null);

  // Form state
  const initialForm = {
    fullName: '',
    email: '',
    mobile: '',
    qualification: 'BE',
    college: '',
    university: '',
    passingYear: new Date().getFullYear(),
    percentage: '',
    cgpa: '',
    backlogCount: 0,
    primaryTechnology: 'Java',
    currentLocation: '',
    preferredLocation: '',
    profileStatus: 'ACTIVE',
  };
  const [formData, setFormData] = useState(initialForm);
  const [formError, setFormError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchCandidates();
  }, [page, search, technology, profileStatus, placementStatus]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchCandidates = async () => {
    setLoading(true);
    try {
      const res = await api.get('/candidates', {
        params: {
          search: search || undefined,
          technology: technology || undefined,
          profileStatus: profileStatus || undefined,
          placementStatus: placementStatus || undefined,
          page,
          size: 10,
        },
      });
      if (res.data.success) {
        setCandidates(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setTotalElements(res.data.data.totalElements);
      }
    } catch (err) {
      console.error('Failed to load candidates', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAdd = () => {
    setFormData(initialForm);
    setFormError('');
    setIsAddOpen(true);
  };

  const handleOpenEdit = (c) => {
    setSelectedCandidate(c);
    setFormData({
      fullName: c.fullName || '',
      email: c.email || '',
      mobile: c.mobile || '',
      qualification: c.qualification || 'BE',
      college: c.college || '',
      university: c.university || '',
      passingYear: c.passingYear || 2025,
      percentage: c.percentage || '',
      cgpa: c.cgpa || '',
      backlogCount: c.backlogCount ?? 0,
      primaryTechnology: c.primaryTechnology || 'Java',
      currentLocation: c.currentLocation || '',
      preferredLocation: c.preferredLocation || '',
      profileStatus: c.profileStatus || 'ACTIVE',
    });
    setFormError('');
    setIsEditOpen(true);
  };

  const handleOpenDetail = (c) => {
    setSelectedCandidate(c);
    setIsDetailOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);

    // Client-side validations preserving rules
    if (!formData.mobile.match(/^\d{10}$/)) {
      setFormError('Mobile must contain exactly 10 digits.');
      setActionLoading(false);
      return;
    }
    const pct = parseFloat(formData.percentage);
    if (isNaN(pct) || pct < 0 || pct > 100) {
      setFormError('Percentage must be a number between 0 and 100.');
      setActionLoading(false);
      return;
    }

    try {
      if (isEditOpen && selectedCandidate) {
        await api.put(`/candidates/${selectedCandidate.candidateId}`, formData);
        showFeedback('Candidate updated successfully!');
        setIsEditOpen(false);
      } else {
        await api.post('/candidates', formData);
        showFeedback('Candidate registered successfully!');
        setIsAddOpen(false);
      }
      fetchCandidates();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Operation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeactivate = async (id) => {
    try {
      await api.patch(`/candidates/${id}/deactivate`);
      showFeedback('Candidate marked as INACTIVE');
      fetchCandidates();
    } catch (err) {
      showFeedback(err.response?.data?.message || 'Failed to deactivate', 'error');
    }
  };

  const handleDelete = async () => {
    if (!selectedCandidate) return;
    setActionLoading(true);
    try {
      await api.delete(`/candidates/${selectedCandidate.candidateId}`);
      showFeedback('Candidate removed successfully');
      setIsDeleteOpen(false);
      fetchCandidates();
    } catch (err) {
      showFeedback(err.response?.data?.message || 'Failed to delete candidate', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Toast Notification */}
      {feedbackMsg.text && (
        <div
          className={`flex items-center gap-2 p-4 rounded-xl border text-sm font-medium ${
            feedbackMsg.type === 'error'
              ? 'bg-rose-50 text-rose-700 border-rose-200'
              : 'bg-emerald-50 text-emerald-700 border-emerald-200'
          }`}
        >
          {feedbackMsg.type === 'error' ? <AlertCircle className="w-5 h-5" /> : <CheckCircle className="w-5 h-5" />}
          <span>{feedbackMsg.text}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <Users className="w-6 h-6 text-indigo-600" />
            Candidate Directory
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Manage candidates, academic qualifications, primary technology, and placement status.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Add Candidate
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
            placeholder="Search by name, code, email, mobile..."
            className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>

        <select
          value={technology}
          onChange={(e) => { setTechnology(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Technologies</option>
          <option value="Java">Java</option>
          <option value="Python">Python</option>
          <option value="Testing">Testing</option>
          <option value="Data Analytics">Data Analytics</option>
          <option value="SQL">SQL</option>
        </select>

        <select
          value={profileStatus}
          onChange={(e) => { setProfileStatus(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Profile Statuses</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="INACTIVE">INACTIVE</option>
          <option value="ON_HOLD">ON_HOLD</option>
          <option value="BLACKLISTED">BLACKLISTED</option>
        </select>

        <select
          value={placementStatus}
          onChange={(e) => { setPlacementStatus(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">All Placement Statuses</option>
          <option value="NOT_PLACED">NOT_PLACED</option>
          <option value="PLACED">PLACED</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50/80 border-b border-slate-200 text-slate-500 font-semibold uppercase tracking-wider">
                <th className="py-3 px-4">Code</th>
                <th className="py-3 px-4">Full Name</th>
                <th className="py-3 px-4">Email / Mobile</th>
                <th className="py-3 px-4">Qualification</th>
                <th className="py-3 px-4">Score</th>
                <th className="py-3 px-4">Tech</th>
                <th className="py-3 px-4">Profile</th>
                <th className="py-3 px-4">Placement</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="9" className="py-8 text-center text-slate-400">Loading candidates...</td>
                </tr>
              ) : candidates.length === 0 ? (
                <tr>
                  <td colSpan="9" className="py-8 text-center text-slate-400">No candidates found matching criteria.</td>
                </tr>
              ) : (
                candidates.map((c) => (
                  <tr key={c.candidateId} className="hover:bg-slate-50/60 transition-colors">
                    <td className="py-3 px-4 font-mono font-bold text-indigo-700">{c.candidateCode}</td>
                    <td className="py-3 px-4 font-semibold text-slate-800">{c.fullName}</td>
                    <td className="py-3 px-4 text-slate-600">
                      <div>{c.email}</div>
                      <div className="text-[11px] text-slate-400">{c.mobile}</div>
                    </td>
                    <td className="py-3 px-4 text-slate-600">{c.qualification} ({c.passingYear})</td>
                    <td className="py-3 px-4">
                      <span className="font-semibold text-slate-800">{c.percentage}%</span>
                      {c.backlogCount > 0 && (
                        <span className="text-[10px] text-rose-600 block font-medium">
                          {c.backlogCount} Backlog{c.backlogCount > 1 ? 's' : ''}
                        </span>
                      )}
                    </td>
                    <td className="py-3 px-4 font-medium text-slate-700">{c.primaryTechnology}</td>
                    <td className="py-3 px-4"><Badge status={c.profileStatus} /></td>
                    <td className="py-3 px-4"><Badge status={c.placementStatus} /></td>
                    <td className="py-3 px-4 text-right space-x-1">
                      <button
                        onClick={() => handleOpenDetail(c)}
                        title="View Details"
                        className="p-1.5 rounded-lg text-slate-500 hover:text-indigo-600 hover:bg-indigo-50"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => handleOpenEdit(c)}
                        title="Edit Candidate"
                        className="p-1.5 rounded-lg text-slate-500 hover:text-amber-600 hover:bg-amber-50"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      {c.profileStatus === 'ACTIVE' && (
                        <button
                          onClick={() => handleDeactivate(c.candidateId)}
                          title="Deactivate"
                          className="p-1.5 rounded-lg text-slate-500 hover:text-rose-600 hover:bg-rose-50"
                        >
                          <PowerOff className="w-4 h-4" />
                        </button>
                      )}
                      <button
                        onClick={() => { setSelectedCandidate(c); setIsDeleteOpen(true); }}
                        title="Delete"
                        className="p-1.5 rounded-lg text-slate-500 hover:text-rose-600 hover:bg-rose-50"
                      >
                        <Trash2 className="w-4 h-4" />
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
          <span>Showing {candidates.length} of {totalElements} candidate records</span>
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

      {/* Add / Edit Modal */}
      <Modal
        isOpen={isAddOpen || isEditOpen}
        onClose={() => { setIsAddOpen(false); setIsEditOpen(false); }}
        title={isEditOpen ? 'Edit Candidate Details' : 'Register New Candidate'}
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
              <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name *</label>
              <input
                type="text"
                required
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Email *</label>
              <input
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Mobile (10 Digits) *</label>
              <input
                type="text"
                required
                pattern="\d{10}"
                value={formData.mobile}
                onChange={(e) => setFormData({ ...formData, mobile: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Primary Technology *</label>
              <select
                value={formData.primaryTechnology}
                onChange={(e) => setFormData({ ...formData, primaryTechnology: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs bg-white focus:ring-2 focus:ring-indigo-500"
              >
                <option value="Java">Java</option>
                <option value="Python">Python</option>
                <option value="Testing">Testing</option>
                <option value="Data Analytics">Data Analytics</option>
                <option value="SQL">SQL</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Qualification *</label>
              <input
                type="text"
                required
                value={formData.qualification}
                onChange={(e) => setFormData({ ...formData, qualification: e.target.value })}
                placeholder="BE, BTech, MCA, etc."
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Passing Year *</label>
              <input
                type="number"
                required
                min="1990"
                max="2035"
                value={formData.passingYear}
                onChange={(e) => setFormData({ ...formData, passingYear: parseInt(e.target.value) || '' })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Percentage (0-100) *</label>
              <input
                type="number"
                step="0.01"
                required
                min="0"
                max="100"
                value={formData.percentage}
                onChange={(e) => setFormData({ ...formData, percentage: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Backlog Count *</label>
              <input
                type="number"
                required
                min="0"
                value={formData.backlogCount}
                onChange={(e) => setFormData({ ...formData, backlogCount: parseInt(e.target.value) || 0 })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">College</label>
              <input
                type="text"
                value={formData.college}
                onChange={(e) => setFormData({ ...formData, college: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Current Location</label>
              <input
                type="text"
                value={formData.currentLocation}
                onChange={(e) => setFormData({ ...formData, currentLocation: e.target.value })}
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
              {actionLoading ? 'Saving...' : 'Save Candidate'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Candidate Detail Modal */}
      {selectedCandidate && (
        <Modal isOpen={isDetailOpen} onClose={() => setIsDetailOpen(false)} title={`Candidate Profile: ${selectedCandidate.candidateCode}`}>
          <div className="space-y-4 text-xs">
            <div className="grid grid-cols-2 gap-3 p-4 bg-slate-50 rounded-xl">
              <div><span className="text-slate-400 block">Full Name:</span> <strong className="text-slate-800 text-sm">{selectedCandidate.fullName}</strong></div>
              <div><span className="text-slate-400 block">Email:</span> <strong className="text-slate-800">{selectedCandidate.email}</strong></div>
              <div><span className="text-slate-400 block">Mobile:</span> <strong className="text-slate-800">{selectedCandidate.mobile}</strong></div>
              <div><span className="text-slate-400 block">Primary Technology:</span> <strong className="text-indigo-600">{selectedCandidate.primaryTechnology}</strong></div>
              <div><span className="text-slate-400 block">Degree / College:</span> {selectedCandidate.qualification} - {selectedCandidate.college || 'N/A'}</div>
              <div><span className="text-slate-400 block">Passing Year:</span> {selectedCandidate.passingYear}</div>
              <div><span className="text-slate-400 block">Academic Percentage:</span> <strong className="text-slate-800">{selectedCandidate.percentage}%</strong></div>
              <div><span className="text-slate-400 block">Active Backlogs:</span> <strong className={selectedCandidate.backlogCount > 0 ? 'text-rose-600' : 'text-emerald-600'}>{selectedCandidate.backlogCount}</strong></div>
              <div><span className="text-slate-400 block">Current Location:</span> {selectedCandidate.currentLocation || 'N/A'}</div>
              <div><span className="text-slate-400 block">Preferred Location:</span> {selectedCandidate.preferredLocation || 'N/A'}</div>
              <div><span className="text-slate-400 block">Profile Status:</span> <Badge status={selectedCandidate.profileStatus} /></div>
              <div><span className="text-slate-400 block">Placement Status:</span> <Badge status={selectedCandidate.placementStatus} /></div>
            </div>
          </div>
        </Modal>
      )}

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title="Delete Candidate Record"
        message={`Are you sure you want to permanently delete candidate "${selectedCandidate?.fullName}" (${selectedCandidate?.candidateCode})? This action cannot be undone.`}
        confirmText="Delete Candidate"
        isDanger={true}
        loading={actionLoading}
      />
    </div>
  );
};
