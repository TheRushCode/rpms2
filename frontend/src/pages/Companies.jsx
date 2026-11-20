import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import {
  Building2,
  Search,
  Plus,
  Edit2,
  Trash2,
  Globe,
  MapPin,
  Mail,
  Phone,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

export const Companies = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [selectedCompany, setSelectedCompany] = useState(null);

  const initialForm = {
    companyName: '',
    industry: 'IT Services',
    website: '',
    location: 'Pune',
    companyType: 'MID_SIZE',
    hrName: '',
    hrEmail: '',
    hrMobile: '',
    status: 'ACTIVE',
  };
  const [formData, setFormData] = useState(initialForm);
  const [formError, setFormError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchCompanies();
  }, [page, search, status]);

  const showFeedback = (text, type = 'success') => {
    setFeedbackMsg({ text, type });
    setTimeout(() => setFeedbackMsg({ text: '', type: '' }), 4000);
  };

  const fetchCompanies = async () => {
    setLoading(true);
    try {
      const res = await api.get('/companies', {
        params: { search: search || undefined, status: status || undefined, page, size: 10 },
      });
      if (res.data.success) {
        setCompanies(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setTotalElements(res.data.data.totalElements);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAdd = () => {
    setFormData(initialForm);
    setFormError('');
    setIsAddOpen(true);
  };

  const handleOpenEdit = (comp) => {
    setSelectedCompany(comp);
    setFormData({
      companyName: comp.companyName || '',
      industry: comp.industry || '',
      website: comp.website || '',
      location: comp.location || '',
      companyType: comp.companyType || 'MID_SIZE',
      hrName: comp.hrName || '',
      hrEmail: comp.hrEmail || '',
      hrMobile: comp.hrMobile || '',
      status: comp.status || 'ACTIVE',
    });
    setFormError('');
    setIsEditOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setFormError('');
    setActionLoading(true);
    try {
      if (isEditOpen && selectedCompany) {
        await api.put(`/companies/${selectedCompany.companyId}`, formData);
        showFeedback('Company updated successfully!');
        setIsEditOpen(false);
      } else {
        await api.post('/companies', formData);
        showFeedback('Company registered successfully!');
        setIsAddOpen(false);
      }
      fetchCompanies();
    } catch (err) {
      setFormError(err.response?.data?.message || err.message || 'Operation failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedCompany) return;
    setActionLoading(true);
    try {
      await api.delete(`/companies/${selectedCompany.companyId}`);
      showFeedback('Company deleted successfully');
      setIsDeleteOpen(false);
      fetchCompanies();
    } catch (err) {
      showFeedback(err.response?.data?.message || 'Failed to delete company', 'error');
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
            <Building2 className="w-6 h-6 text-indigo-600" />
            Corporate Partner Companies
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Recruiting partners, industry domains, and HR contacts.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="inline-flex items-center justify-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-sm font-semibold shadow-md shadow-indigo-600/30 transition-all"
        >
          <Plus className="w-4 h-4" />
          Add Company
        </button>
      </div>

      {/* Filter */}
      <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-200/80 flex items-center gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            placeholder="Search company name, industry, location..."
            className="w-full pl-9 pr-3 py-2 text-xs rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white"
        >
          <option value="">All Statuses</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="INACTIVE">INACTIVE</option>
        </select>
      </div>

      {/* Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {loading ? (
          <div className="col-span-3 py-16 text-center text-slate-400">Loading companies...</div>
        ) : companies.length === 0 ? (
          <div className="col-span-3 py-16 text-center text-slate-400">No companies found.</div>
        ) : (
          companies.map((c) => (
            <div key={c.companyId} className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 hover:shadow-md transition-shadow flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <div>
                    <span className="text-[10px] font-mono font-bold text-indigo-700 bg-indigo-50 px-2 py-0.5 rounded border border-indigo-200">
                      {c.companyCode}
                    </span>
                    <h3 className="font-bold text-slate-900 text-sm mt-1">{c.companyName}</h3>
                    <p className="text-xs text-slate-500">{c.industry} &bull; {c.companyType}</p>
                  </div>
                  <Badge status={c.status} />
                </div>

                <div className="space-y-1.5 text-xs text-slate-600 border-t border-slate-100 pt-3">
                  <div className="flex items-center gap-1.5">
                    <MapPin className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                    <span>{c.location || 'Location not specified'}</span>
                  </div>
                  {c.website && (
                    <div className="flex items-center gap-1.5">
                      <Globe className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                      <a href={c.website} target="_blank" rel="noreferrer" className="text-indigo-600 hover:underline truncate">
                        {c.website}
                      </a>
                    </div>
                  )}
                  <div className="flex items-center gap-1.5 pt-2 border-t border-slate-50">
                    <Mail className="w-3.5 h-3.5 text-slate-400 shrink-0" />
                    <span>{c.hrName || 'HR'} ({c.hrEmail || 'No email'})</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-end gap-1 pt-4 mt-4 border-t border-slate-100">
                <button
                  onClick={() => handleOpenEdit(c)}
                  className="p-1.5 rounded-lg text-slate-500 hover:text-amber-600 hover:bg-amber-50"
                >
                  <Edit2 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => { setSelectedCompany(c); setIsDeleteOpen(true); }}
                  className="p-1.5 rounded-lg text-slate-500 hover:text-rose-600 hover:bg-rose-50"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Modal */}
      <Modal
        isOpen={isAddOpen || isEditOpen}
        onClose={() => { setIsAddOpen(false); setIsEditOpen(false); }}
        title={isEditOpen ? 'Edit Company Information' : 'Add New Corporate Partner'}
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
              <label className="block text-xs font-semibold text-slate-700 mb-1">Company Name *</label>
              <input
                type="text"
                required
                value={formData.companyName}
                onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Industry</label>
              <input
                type="text"
                value={formData.industry}
                onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Location</label>
              <input
                type="text"
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Website URL</label>
              <input
                type="url"
                value={formData.website}
                onChange={(e) => setFormData({ ...formData, website: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">HR Contact Name</label>
              <input
                type="text"
                value={formData.hrName}
                onChange={(e) => setFormData({ ...formData, hrName: e.target.value })}
                className="w-full p-2.5 rounded-xl border border-slate-200 text-xs focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">HR Email</label>
              <input
                type="email"
                value={formData.hrEmail}
                onChange={(e) => setFormData({ ...formData, hrEmail: e.target.value })}
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
              {actionLoading ? 'Saving...' : 'Save Company'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Dialog */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
        title="Delete Company Record"
        message={`Are you sure you want to delete ${selectedCompany?.companyName}? If jobs are attached to this company, deletion will be blocked.`}
        confirmText="Delete"
        isDanger={true}
        loading={actionLoading}
      />
    </div>
  );
};
