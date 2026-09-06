import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react';
import { api } from '../services/api';

const HistoryPanel = forwardRef(({ userId, onLoadCode }, ref) => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [loadingId, setLoadingId] = useState(null);
  const [activeId, setActiveId] = useState(null);

  const loadHistory = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getHistory(userId);
      setHistory(Array.isArray(data) ? data : []);
    } catch (err) {
      setError('Failed to load history');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useImperativeHandle(ref, () => ({
    refresh: loadHistory,
  }));

  useEffect(() => {
    if (userId) {
      loadHistory();
    }
  }, [userId]);

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

  const getStatusColor = (status) => {
    switch (status) {
      case 'SUCCESS':
        return '#10b981';
      case 'COMPILATION_ERROR':
      case 'RUNTIME_ERROR':
      case 'ERROR':
        return '#ef4444';
      case 'TIMEOUT':
        return '#f59e0b';
      default:
        return '#6b7280';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return String(dateString);
    return date.toLocaleString();
  };

  return (
    <div className="history-panel">
      <div className="history-header">
        <h3>History</h3>
        <button
          type="button"
          onClick={loadHistory}
          className="btn btn-sm btn-secondary"
          disabled={loading}
        >
          Refresh
        </button>
      </div>

      {loading && (
        <div className="loading">
          <span className="spinner spinner-sm" aria-hidden="true" />
          Loading history...
        </div>
      )}
      {error && <div className="error">{error}</div>}

      <div className="history-list">
        {history.length === 0 && !loading && (
          <div className="empty-state">No submissions yet</div>
        )}

        {history.map((item) => (
          <div
            key={item.id}
            className={`history-item${activeId === item.id ? ' active' : ''}`}
            onClick={() => handleLoadCode(item)}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                handleLoadCode(item);
              }
            }}
          >
            <div className="history-item-header">
              <span className="language-badge">{item.language}</span>
              <span
                className="status-badge"
                style={{ backgroundColor: getStatusColor(item.status) }}
              >
                {item.status}
              </span>
            </div>
            <div className="history-item-time">
              {formatDate(item.createdAt)}
              {item.executionTime != null ? ` · ${item.executionTime} ms` : ''}
              {loadingId === item.id ? ' · Loading...' : ''}
            </div>
            {item.output && (
              <div className="history-item-output">
                {item.output.substring(0, 50)}
                {item.output.length > 50 ? '...' : ''}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
});

HistoryPanel.displayName = 'HistoryPanel';

export default HistoryPanel;
