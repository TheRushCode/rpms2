import React from 'react';

export const Badge = ({ status, variant }) => {
  const getStyle = (val) => {
    switch (val?.toUpperCase()) {
      case 'ACTIVE':
      case 'OPEN':
      case 'ELIGIBLE':
      case 'SELECTED':
      case 'ACCEPTED':
      case 'JOINED':
      case 'COMPLETED':
      case 'PLACED':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
      
      case 'SHORTLISTED':
      case 'INTERVIEW_SCHEDULED':
      case 'SCHEDULED':
      case 'OFFERED':
      case 'ELIGIBILITY_VERIFIED':
        return 'bg-blue-50 text-blue-700 border-blue-200';

      case 'APPLIED':
      case 'PENDING':
      case 'ON_HOLD':
      case 'DRAFT':
      case 'NEXT_ROUND':
        return 'bg-amber-50 text-amber-700 border-amber-200';

      case 'INACTIVE':
      case 'CLOSED':
      case 'NOT_ELIGIBLE':
      case 'REJECTED':
      case 'EXPIRED':
      case 'WITHDRAWN':
      case 'CANCELLED':
      case 'NOT_JOINED':
      case 'NO_SHOW':
      case 'BLACKLISTED':
        return 'bg-rose-50 text-rose-700 border-rose-200';

      case 'NOT_PLACED':
        return 'bg-slate-100 text-slate-700 border-slate-200';

      default:
        return 'bg-slate-100 text-slate-700 border-slate-200';
    }
  };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${getStyle(status)}`}>
      {status || 'Unknown'}
    </span>
  );
};
