import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react';
import { api } from '../services/api';

const STATUS_META = {
  SUCCESS: { label: 'Success', tone: 'border-emerald-500/25 bg-emerald-500/10 text-emerald-300' },
  COMPILATION_ERROR: { label: 'Compilation Error', tone: 'border-rose-500/25 bg-rose-500/10 text-rose-300' },
  RUNTIME_ERROR: { label: 'Runtime Error', tone: 'border-rose-500/25 bg-rose-500/10 text-rose-300' },
  TIMEOUT: { label: 'Timeout', tone: 'border-amber-500/25 bg-amber-500/10 text-amber-300' },
  ERROR: { label: 'Error', tone: 'border-rose-500/25 bg-rose-500/10 text-rose-300' },
};

const HistoryPanel = forwardRef(({ onLoadCode }, ref) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [loadingId, setLoadingId] = useState(null);
  const [activeId, setActiveId] = useState(null);

  const loadHistory = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getHistory();
      setHistory(Array.isArray(data) ? data : []);
    } catch (err) {
      setError('Failed to load history');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const clearHistory = () => {
    setHistory([]);
    setError(null);
    setActiveId(null);
  };

  useImperativeHandle(ref, () => ({
    refresh: loadHistory,
    clearHistory,
  }));

  useEffect(() => {
    loadHistory();
  }, []);

  const handleLoadCode = async (item) => {
    setLoadingId(item.id);
    setError(null);
    try {
      const code = await api.getCodeById(item.id);
      onLoadCode({
        code,
        language: item.language,
      });
      setActiveId(item.id);
    } catch (err) {
      console.error('Failed to load code:', err);
      setError('Failed to load code for this submission');
    } finally {
      setLoadingId(null);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return String(dateString);
    return date.toLocaleString();
  };

  const getStatusMeta = (status) => STATUS_META[status] || {
    label: String(status || 'Unknown').replace(/_/g, ' '),
    tone: 'border-slate-700 bg-slate-800 text-slate-300',
  };

  return (
    <section className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/80 shadow-2xl shadow-black/20 backdrop-blur">
      <div className="flex items-center justify-between border-b border-slate-800 bg-slate-900/70 px-4 py-3 sm:px-5">
        <div>
          <h2 className="text-sm font-semibold text-slate-100">History</h2>
          <p className="mt-1 text-xs text-slate-400">Recent submissions and outcomes</p>
        </div>
        <button
          type="button"
          onClick={loadHistory}
          disabled={loading}
          className="inline-flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900 px-3 py-2 text-xs font-medium text-slate-200 transition-all duration-200 hover:border-slate-600 hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <svg className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
            <path d="M4 4v6h6" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M20 20v-6h-6" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M20 8a8 8 0 00-13.66-5.66L4 4" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M4 16a8 8 0 0013.66 5.66L20 20" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          Refresh
        </button>
      </div>

      <div className="min-h-0 flex-1 overflow-auto px-3 py-3 sm:px-4">
        {loading && (
          <div className="flex items-center justify-center gap-2 rounded-2xl border border-slate-800 bg-slate-950/60 px-4 py-8 text-sm text-slate-400">
            <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
            Loading history...
          </div>
        )}

        {!loading && error && (
          <div className="rounded-2xl border border-rose-500/20 bg-rose-500/10 px-4 py-6 text-sm text-rose-300">
            {error}
          </div>
        )}

        {!loading && !error && history.length === 0 && (
          <div className="rounded-2xl border border-dashed border-slate-800 bg-slate-950/60 px-4 py-8 text-center text-sm text-slate-500">
            No submissions yet
          </div>
        )}

        {!loading && history.length > 0 && (
          <div className="space-y-3">
            {history.map((item) => {
              const meta = getStatusMeta(item.status);
              return (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => handleLoadCode(item)}
                  className={`w-full rounded-2xl border p-4 text-left transition-all duration-200 hover:-translate-y-0.5 hover:border-sky-500/40 hover:bg-slate-900/90 ${
                    activeId === item.id
                      ? 'border-sky-500/40 bg-slate-900/95 shadow-lg shadow-sky-500/10'
                      : 'border-slate-800 bg-slate-950/70'
                  }`}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <div className="flex items-center gap-2">
                        <span className="rounded-full border border-slate-700 bg-slate-900 px-2.5 py-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-200">
                          {String(item.language || 'unknown').toUpperCase()}
                        </span>
                        <span className={`rounded-full border px-2.5 py-1 text-[11px] font-semibold uppercase tracking-[0.16em] ${meta.tone}`}>
                          {meta.label}
                        </span>
                      </div>

                      <div className="mt-3 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-400">
                        <span>{formatDate(item.createdAt)}</span>
                        {item.executionTime != null && <span>{item.executionTime} ms</span>}
                        {loadingId === item.id && <span className="text-sky-300">Loading...</span>}
                      </div>
                    </div>

                    <svg className="mt-1 h-4 w-4 shrink-0 text-slate-500" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.8">
                      <path d="M8 5l5 5-5 5" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                  </div>

                  <div
                    className="mt-3 text-sm leading-6 text-slate-300"
                    style={{
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden',
                    }}
                  >
                    {item.output ? item.output.slice(0, 140) : 'No output captured for this submission.'}
                    {item.output && item.output.length > 140 ? '...' : ''}
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
});

HistoryPanel.displayName = 'HistoryPanel';

export default HistoryPanel;
