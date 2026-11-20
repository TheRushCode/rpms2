import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Activity, Search, Filter } from 'lucide-react';

export const ActivityLogs = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [module, setModule] = useState('');
  const [action, setAction] = useState('');

  useEffect(() => {
    fetchLogs();
  }, [page, module, action]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const res = await api.get('/activity-logs', {
        params: { module: module || undefined, action: action || undefined, page, size: 20 },
      });
      if (res.data.success) {
        setLogs(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h2 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
          <Activity className="w-6 h-6 text-indigo-600" />
          System Activity &amp; Audit Trail
        </h2>
        <p className="text-xs text-slate-500 mt-0.5">
          Immutable audit log recording user actions, status modifications, and system events.
        </p>
      </div>

      {/* Filter */}
      <div className="bg-white p-4 rounded-2xl shadow-sm border border-slate-200/80 flex items-center gap-3">
        <select
          value={module}
          onChange={(e) => { setModule(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white"
        >
          <option value="">All Modules</option>
          <option value="USER">USER</option>
          <option value="CANDIDATE">CANDIDATE</option>
          <option value="JOB">JOB</option>
          <option value="APPLICATION">APPLICATION</option>
          <option value="INTERVIEW">INTERVIEW</option>
          <option value="OFFER">OFFER</option>
          <option value="PLACEMENT">PLACEMENT</option>
        </select>

        <select
          value={action}
          onChange={(e) => { setAction(e.target.value); setPage(0); }}
          className="px-3 py-2 text-xs rounded-xl border border-slate-200 bg-white"
        >
          <option value="">All Actions</option>
          <option value="LOGIN">LOGIN</option>
          <option value="CREATE">CREATE</option>
          <option value="UPDATE">UPDATE</option>
          <option value="STATUS_CHANGE">STATUS_CHANGE</option>
          <option value="DEACTIVATE">DEACTIVATE</option>
          <option value="DELETE">DELETE</option>
        </select>
      </div>

      {/* List */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200/80 divide-y divide-slate-100">
        {loading ? (
          <div className="p-8 text-center text-slate-400 text-xs">Loading audit logs...</div>
        ) : logs.length === 0 ? (
          <div className="p-8 text-center text-slate-400 text-xs">No activity logs found.</div>
        ) : (
          logs.map((log) => (
            <div key={log.logId} className="p-4 flex items-start gap-4 hover:bg-slate-50/60 transition-colors">
              <span className="px-2 py-0.5 text-[10px] font-bold rounded bg-slate-100 text-slate-600 border border-slate-200 uppercase shrink-0 mt-0.5">
                {log.moduleName}
              </span>
              <div className="flex-1 min-w-0">
                <p className="text-xs font-semibold text-slate-800">{log.description}</p>
                <div className="flex items-center gap-2 text-[11px] text-slate-400 mt-1">
                  <span>Action: <strong>{log.actionName}</strong></span>
                  {log.recordId && <span>&bull; Record ID: <strong>{log.recordId}</strong></span>}
                  <span>&bull; Timestamp: <strong>{new Date(log.loggedAt).toLocaleString()}</strong></span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
