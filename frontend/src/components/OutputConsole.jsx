import React from 'react';

const statusStyles = {
  SUCCESS: 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300',
  COMPILATION_ERROR: 'border-rose-500/30 bg-rose-500/10 text-rose-300',
  RUNTIME_ERROR: 'border-rose-500/30 bg-rose-500/10 text-rose-300',
  TIMEOUT: 'border-amber-500/30 bg-amber-500/10 text-amber-300',
  ERROR: 'border-rose-500/30 bg-rose-500/10 text-rose-300',
};

const OutputConsole = ({ stdout, stderr, status, executionTime, isRunning }) => {
  const hasStdout = Boolean(stdout && stdout.trim());
  const hasStderr = Boolean(stderr && stderr.trim());
  const isEmpty = !isRunning && !hasStdout && !hasStderr;
  const badgeClass = statusStyles[status] || 'border-slate-700 bg-slate-800 text-slate-300';

  return (
    <section className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/80 shadow-2xl shadow-black/20 backdrop-blur">
      <div className="flex items-center justify-between border-b border-slate-800 bg-slate-900/70 px-4 py-3 sm:px-5">
        <div className="min-w-0">
          <h2 className="text-sm font-semibold text-slate-100">Output</h2>
          <p className="mt-1 text-xs text-slate-400">Terminal-style runtime result</p>
        </div>
        {status && (
          <span className={`rounded-full border px-2.5 py-1 text-[11px] font-semibold uppercase tracking-[0.16em] ${badgeClass}`}>
            {status.replace(/_/g, ' ')}
          </span>
        )}
      </div>

      <div className="min-h-0 flex-1 overflow-auto bg-[#020617] px-4 py-4 font-mono text-sm text-slate-200 sm:px-5">
        <div className="mb-4 flex items-center gap-2 text-xs text-slate-500">
          <span className="h-2.5 w-2.5 rounded-full bg-rose-400" />
          <span className="h-2.5 w-2.5 rounded-full bg-amber-400" />
          <span className="h-2.5 w-2.5 rounded-full bg-emerald-400" />
          <span className="ml-2 text-slate-500">$ execute</span>
        </div>

        {isRunning && (
          <div className="mb-4 flex items-center gap-2 text-slate-400">
            <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
            <span>Running code...</span>
          </div>
        )}

        {isEmpty && (
          <div className="rounded-2xl border border-dashed border-slate-800 bg-slate-950/60 px-4 py-6 text-slate-500">
            No output yet. Run your code to see results.
          </div>
        )}

        {!isRunning && hasStdout && (
          <div className="mb-5">
            <div className="mb-2 flex items-center gap-2 text-xs uppercase tracking-[0.18em] text-sky-300">
              <span className="h-2 w-2 rounded-full bg-sky-400" />
              stdout
            </div>
            <pre className="whitespace-pre-wrap break-words rounded-2xl border border-slate-800 bg-slate-950/80 px-4 py-4 leading-relaxed text-emerald-300">
              {stdout}
            </pre>
          </div>
        )}

        {!isRunning && hasStderr && (
          <div>
            <div className="mb-2 flex items-center gap-2 text-xs uppercase tracking-[0.18em] text-rose-300">
              <span className="h-2 w-2 rounded-full bg-rose-400" />
              stderr
            </div>
            <pre className="whitespace-pre-wrap break-words rounded-2xl border border-rose-500/20 bg-rose-500/10 px-4 py-4 leading-relaxed text-rose-200">
              {stderr}
            </pre>
          </div>
        )}
      </div>

      <div className="flex items-center justify-between border-t border-slate-800 bg-slate-900/70 px-4 py-3 text-xs text-slate-400 sm:px-5">
        {executionTime !== null && executionTime !== undefined && !isRunning ? (
          <span>
            Execution time <span className="text-slate-100">{executionTime} ms</span>
          </span>
        ) : (
          <span>{isRunning ? 'Measuring execution time...' : 'Execution time unavailable'}</span>
        )}
        <span className="text-slate-500">
          {status ? status.replace(/_/g, ' ').toLowerCase() : 'idle'}
        </span>
      </div>
    </section>
  );
};

export default OutputConsole;
