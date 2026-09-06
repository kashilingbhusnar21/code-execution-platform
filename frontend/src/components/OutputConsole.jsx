import React from 'react';

const OutputConsole = ({ stdout, stderr, status, executionTime, isRunning }) => {
  const getStatusColor = (value) => {
    switch (value) {
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

  const hasStdout = Boolean(stdout && stdout.trim());
  const hasStderr = Boolean(stderr && stderr.trim());
  const isEmpty = !isRunning && !hasStdout && !hasStderr;

  return (
    <div className="output-console">
      <div className="console-header">
        <h3>Output</h3>
        {status && (
          <div
            className="status-badge fade-in"
            style={{ backgroundColor: getStatusColor(status) }}
          >
            {status}
          </div>
        )}
      </div>

      <div className="console-output">
        {isRunning && (
          <div className="console-running fade-in">
            <span className="spinner" aria-hidden="true" />
            <span>Running...</span>
          </div>
        )}

        {isEmpty && (
          <pre className="console-placeholder">
            No output yet. Run your code to see results.
          </pre>
        )}

        {!isRunning && hasStdout && (
          <div className="console-block fade-in">
            <div className="console-label">stdout</div>
            <pre className="stdout-text">{stdout}</pre>
          </div>
        )}

        {!isRunning && hasStderr && (
          <div className="console-block fade-in">
            <div className="console-label console-label-error">stderr</div>
            <pre className="stderr-text">{stderr}</pre>
          </div>
        )}
      </div>

      <div className="execution-info">
        {executionTime !== null && executionTime !== undefined && !isRunning ? (
          <span className="execution-time fade-in">
            Execution Time: {executionTime} ms
          </span>
        ) : (
          <span className="execution-time muted">
            {isRunning ? 'Measuring execution time...' : 'Execution Time: —'}
          </span>
        )}
      </div>
    </div>
  );
};

export default OutputConsole;
