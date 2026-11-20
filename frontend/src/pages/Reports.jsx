import React, { useState, useEffect } from 'react';
import api from '../services/api';
import {
  BarChart3,
  Download,
  FileSpreadsheet,
  PieChart,
  TrendingUp,
  CheckCircle2
} from 'lucide-react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement
} from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement);

export const Reports = () => {
  const [techReport, setTechReport] = useState({});
  const [appStatusReport, setAppStatusReport] = useState([]);
  const [companyReport, setCompanyReport] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    try {
      const [techRes, appRes, compRes] = await Promise.all([
        api.get('/reports/candidate-technology'),
        api.get('/reports/applications-by-status'),
        api.get('/reports/placements-by-company')
      ]);
      if (techRes.data.success) setTechReport(techRes.data.data);
      if (appRes.data.success) setAppStatusReport(appRes.data.data);
      if (compRes.data.success) setCompanyReport(compRes.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const downloadCsv = async (endpoint, filename) => {
    try {
      const res = await api.get(endpoint, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (e) {
      console.error('Download failed', e);
    }
  };

  const techLabels = Object.keys(techReport);
  const techValues = Object.values(techReport);

  const techChartData = {
    labels: techLabels,
    datasets: [
      {
        data: techValues,
        backgroundColor: ['#6366f1', '#10b981', '#3b82f6', '#f59e0b', '#ec4899', '#8b5cf6'],
        borderWidth: 0,
      },
    ],
  };

  const compChartData = {
    labels: companyReport.map((c) => c.companyName),
    datasets: [
      {
        label: 'Placements Count',
        data: companyReport.map((c) => c.placedCount),
        backgroundColor: '#10b981',
        borderRadius: 6,
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
            <BarChart3 className="w-6 h-6 text-indigo-600" />
            Recruitment Reports &amp; Analytics
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Preserves the original Technology Distribution report, hiring funnels, and data export.
          </p>
        </div>

        {/* CSV Export Buttons */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => downloadCsv('/reports/candidates/csv', 'candidates.csv')}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 rounded-xl text-xs font-semibold shadow-sm transition-all"
          >
            <FileSpreadsheet className="w-4 h-4 text-emerald-600" />
            Candidates CSV
          </button>
          <button
            onClick={() => downloadCsv('/reports/jobs/csv', 'jobs.csv')}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 rounded-xl text-xs font-semibold shadow-sm transition-all"
          >
            <FileSpreadsheet className="w-4 h-4 text-indigo-600" />
            Jobs CSV
          </button>
          <button
            onClick={() => downloadCsv('/reports/placements/csv', 'placements.csv')}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 rounded-xl text-xs font-semibold shadow-sm transition-all"
          >
            <FileSpreadsheet className="w-4 h-4 text-amber-600" />
            Placements CSV
          </button>
        </div>
      </div>

      {/* Reports Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Candidate Technology Report (Starter Option 5) */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80">
          <h3 className="font-semibold text-slate-900 text-sm mb-1">
            Candidate Technology Report (Preserved from starter)
          </h3>
          <p className="text-xs text-slate-500 mb-4">Total candidate count grouped by primary technology domain.</p>

          <div className="h-64 flex items-center justify-center">
            {techLabels.length > 0 ? (
              <Doughnut
                data={techChartData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } } },
                }}
              />
            ) : (
              <p className="text-xs text-slate-400">Loading...</p>
            )}
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 mt-4 pt-4 border-t border-slate-100">
            {Object.entries(techReport).map(([tech, cnt]) => (
              <div key={tech} className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 text-center">
                <span className="text-[11px] text-slate-500 block truncate">{tech}</span>
                <span className="text-base font-bold text-slate-800">{cnt}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Company-wise Placement Report */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80">
          <h3 className="font-semibold text-slate-900 text-sm mb-1">
            Company-Wise Placements Report
          </h3>
          <p className="text-xs text-slate-500 mb-4">Final placed count per corporate hiring partner.</p>

          <div className="h-64">
            {companyReport.length > 0 ? (
              <Bar
                data={compChartData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { display: false } },
                  scales: { y: { beginAtZero: true } },
                }}
              />
            ) : (
              <p className="text-xs text-slate-400 text-center pt-24">No placement data</p>
            )}
          </div>

          <div className="mt-4 pt-4 border-t border-slate-100 space-y-2">
            {companyReport.map((c, idx) => (
              <div key={idx} className="flex items-center justify-between text-xs py-1">
                <span className="text-slate-700 font-medium">{c.companyName}</span>
                <span className="font-bold text-emerald-600">{c.placedCount} joined</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
