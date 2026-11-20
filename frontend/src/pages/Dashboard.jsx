import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { StatCard } from '../components/common/StatCard';
import {
  Users,
  Briefcase,
  FileText,
  Award,
  Building2,
  CheckCircle2,
  Activity,
  ArrowUpRight
} from 'lucide-react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title
} from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';
import { Link } from 'react-router-dom';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title);

export const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const res = await api.get('/dashboard/stats');
      if (res.data.success) {
        setStats(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load dashboard stats', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  // Technology chart data
  const techData = {
    labels: stats?.candidateTechnologyDistribution?.map((t) => t.technology) || [],
    datasets: [
      {
        data: stats?.candidateTechnologyDistribution?.map((t) => t.count) || [],
        backgroundColor: [
          '#6366f1', '#10b981', '#3b82f6', '#f59e0b', '#ec4899', '#8b5cf6', '#14b8a6'
        ],
        borderWidth: 0,
      },
    ],
  };

  // Applications status chart data
  const appData = {
    labels: stats?.applicationsByStatus?.map((a) => a.status) || [],
    datasets: [
      {
        label: 'Applications',
        data: stats?.applicationsByStatus?.map((a) => a.count) || [],
        backgroundColor: '#4f46e5',
        borderRadius: 6,
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-indigo-700 via-indigo-600 to-indigo-800 rounded-2xl p-6 sm:p-8 text-white shadow-lg">
        <h2 className="text-2xl font-bold tracking-tight">Recruitment &amp; Placement Overview</h2>
        <p className="text-indigo-100 text-sm mt-1 max-w-2xl">
          Welcome to RPMS. Track candidate registrations, corporate hiring pipelines, eligibility verification, interviews, and final placements.
        </p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Candidates"
          value={stats?.totalCandidates || 0}
          subtext={`${stats?.placedCandidates || 0} placed`}
          icon={Users}
          color="indigo"
        />
        <StatCard
          title="Active Job Openings"
          value={stats?.openJobs || 0}
          subtext={`${stats?.totalJobs || 0} total postings`}
          icon={Briefcase}
          color="emerald"
        />
        <StatCard
          title="Total Applications"
          value={stats?.totalApplications || 0}
          subtext="Workflow in progress"
          icon={FileText}
          color="blue"
        />
        <StatCard
          title="Placed Candidates"
          value={stats?.activePlacements || 0}
          subtext="Joined & Verified"
          icon={CheckCircle2}
          color="amber"
        />
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Technology Distribution */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 text-sm">Candidates by Technology</h3>
            <Link to="/reports" className="text-xs text-indigo-600 hover:text-indigo-700 font-medium flex items-center gap-0.5">
              View All <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
          <div className="h-64 flex items-center justify-center">
            {stats?.candidateTechnologyDistribution?.length > 0 ? (
              <Doughnut
                data={techData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } } },
                }}
              />
            ) : (
              <p className="text-sm text-slate-400">No technology data</p>
            )}
          </div>
        </div>

        {/* Applications Status Breakdown */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80 lg:col-span-2">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 text-sm">Applications by Workflow Status</h3>
            <Link to="/applications" className="text-xs text-indigo-600 hover:text-indigo-700 font-medium flex items-center gap-0.5">
              View Applications <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
          <div className="h-64">
            {stats?.applicationsByStatus?.length > 0 ? (
              <Bar
                data={appData}
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { display: false } },
                  scales: { y: { beginAtZero: true, grid: { color: '#f1f5f9' } }, x: { grid: { display: false } } },
                }}
              />
            ) : (
              <p className="text-sm text-slate-400 text-center pt-24">No applications data</p>
            )}
          </div>
        </div>
      </div>

      {/* Recent Activities & Quick Navigation */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Activities Feed */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80 lg:col-span-2">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Activity className="w-4 h-4 text-indigo-600" />
              <h3 className="font-semibold text-slate-900 text-sm">Recent System Activities</h3>
            </div>
            <Link to="/activity-logs" className="text-xs text-indigo-600 hover:text-indigo-700 font-medium flex items-center gap-0.5">
              Full Audit Log <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="divide-y divide-slate-100 max-h-80 overflow-y-auto">
            {stats?.recentActivities?.length > 0 ? (
              stats.recentActivities.map((act) => (
                <div key={act.logId} className="py-3 flex items-start gap-3 text-sm">
                  <span className="px-2 py-0.5 text-[10px] font-bold rounded bg-slate-100 text-slate-600 border border-slate-200 uppercase shrink-0 mt-0.5">
                    {act.module}
                  </span>
                  <div className="flex-1 min-w-0">
                    <p className="text-slate-800 text-xs font-medium">{act.description}</p>
                    <p className="text-[11px] text-slate-400 mt-0.5">
                      {act.action} &bull; {new Date(act.loggedAt).toLocaleString()}
                    </p>
                  </div>
                </div>
              ))
            ) : (
              <p className="text-sm text-slate-400 text-center py-8">No recent activity</p>
            )}
          </div>
        </div>

        {/* Company Placement Highlights */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200/80">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 text-sm">Top Hiring Companies</h3>
            <Link to="/placements" className="text-xs text-indigo-600 hover:text-indigo-700 font-medium flex items-center gap-0.5">
              Placements <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>
          <div className="space-y-3">
            {stats?.placementsByCompany?.slice(0, 5).map((comp, idx) => (
              <div key={idx} className="flex items-center justify-between p-2.5 rounded-xl bg-slate-50 border border-slate-100">
                <div className="flex items-center gap-2.5">
                  <div className="w-7 h-7 rounded-lg bg-indigo-100 text-indigo-700 flex items-center justify-center font-bold text-xs">
                    {idx + 1}
                  </div>
                  <span className="text-xs font-semibold text-slate-800">{comp.companyName}</span>
                </div>
                <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                  {comp.placedCount} Placed
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
