import React from 'react';

/**
 * LeetCode-styled DSA Solving Progress Card
 * Features:
 * - Center: Multi-segment circular/donut progress indicator showing Solved / Total,
 *   green checkmark with 'Solved' label, and 'Attempting' count underneath.
 * - Right: Vertically stacked difficulty summaries (Easy, Medium, Hard) with subtle themed colors.
 * - Preserves topic breakdown and responsive layout.
 */
const DsaProgressCard = ({ userProgress, problems = [], attemptingCount = 0 }) => {
  // 1. Calculate overall counts safely
  const totalProblems = userProgress?.totalProblems ?? problems.length ?? 0;
  const solvedCount = userProgress?.solvedProblems ?? (userProgress?.solvedProblemIds?.length || 0);
  const overallPercentage = totalProblems > 0
    ? Math.round((solvedCount / totalProblems) * 1000) / 10
    : 0;

  // 2. Derive difficulty stats (from userProgress.difficultyStats or fallback to problems list)
  const solvedSet = new Set((userProgress?.solvedProblemIds || []).map(String));

  const easyTotal = userProgress?.difficultyStats?.EASY?.total 
    ?? problems.filter((p) => p.difficulty === 'EASY').length;
  const easySolved = userProgress?.difficultyStats?.EASY?.solved 
    ?? problems.filter((p) => p.difficulty === 'EASY' && solvedSet.has(String(p.id))).length;

  const mediumTotal = userProgress?.difficultyStats?.MEDIUM?.total 
    ?? problems.filter((p) => p.difficulty === 'MEDIUM').length;
  const mediumSolved = userProgress?.difficultyStats?.MEDIUM?.solved 
    ?? problems.filter((p) => p.difficulty === 'MEDIUM' && solvedSet.has(String(p.id))).length;

  const hardTotal = userProgress?.difficultyStats?.HARD?.total 
    ?? problems.filter((p) => p.difficulty === 'HARD').length;
  const hardSolved = userProgress?.difficultyStats?.HARD?.solved 
    ?? problems.filter((p) => p.difficulty === 'HARD' && solvedSet.has(String(p.id))).length;

  const easyPercent = easyTotal > 0 ? Math.min(100, Math.round((easySolved / easyTotal) * 100)) : 0;
  const mediumPercent = mediumTotal > 0 ? Math.min(100, Math.round((mediumSolved / mediumTotal) * 100)) : 0;
  const hardPercent = hardTotal > 0 ? Math.min(100, Math.round((hardSolved / hardTotal) * 100)) : 0;

  // 3. Circular SVG Donut Geometry
  const size = 150;
  const strokeWidth = 10;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;

  const safeTotal = totalProblems > 0 ? totalProblems : 1;
  const easyArc = (easySolved / safeTotal) * circumference;
  const medArc = (mediumSolved / safeTotal) * circumference;
  const hardArc = (hardSolved / safeTotal) * circumference;

  const medOffset = -easyArc;
  const hardOffset = -(easyArc + medArc);

  return (
    <div className="mb-8 rounded-2xl border border-slate-800/90 bg-slate-900/70 p-6 backdrop-blur-xl shadow-xl shadow-black/20">
      {/* Card Header */}
      <div className="flex items-center justify-between border-b border-slate-800/80 pb-3 mb-5">
        <div className="flex items-center gap-2">
          <div className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />
          <span className="text-xs font-semibold uppercase tracking-wider text-slate-300">
            DSA Solving Progress
          </span>
        </div>
        <span className="rounded-full border border-cyan-500/30 bg-cyan-500/10 px-2.5 py-0.5 text-xs font-bold text-cyan-300">
          {overallPercentage}%
        </span>
      </div>

      <div className="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
        {/* Left/Center: Circular Donut Indicator */}
        <div className="flex flex-col items-center justify-center min-w-[200px] py-1">
          <div className="relative" style={{ width: size, height: size }}>
            <svg
              width={size}
              height={size}
              className="transform -rotate-90"
              viewBox={`0 0 ${size} ${size}`}
            >
              {/* Background circular track */}
              <circle
                cx={size / 2}
                cy={size / 2}
                r={radius}
                fill="transparent"
                stroke="rgb(30 41 59)"
                strokeWidth={strokeWidth}
              />

              {/* Easy Arc (Cyan / Teal) */}
              {easyArc > 0 && (
                <circle
                  cx={size / 2}
                  cy={size / 2}
                  r={radius}
                  fill="transparent"
                  stroke="#06b6d4"
                  strokeWidth={strokeWidth}
                  strokeDasharray={`${easyArc} ${circumference - easyArc}`}
                  strokeDashoffset={0}
                  strokeLinecap="butt"
                  className="transition-all duration-700 ease-out"
                />
              )}

              {/* Medium Arc (Amber / Orange) */}
              {medArc > 0 && (
                <circle
                  cx={size / 2}
                  cy={size / 2}
                  r={radius}
                  fill="transparent"
                  stroke="#f59e0b"
                  strokeWidth={strokeWidth}
                  strokeDasharray={`${medArc} ${circumference - medArc}`}
                  strokeDashoffset={medOffset}
                  strokeLinecap="butt"
                  className="transition-all duration-700 ease-out"
                />
              )}

              {/* Hard Arc (Rose / Pink) */}
              {hardArc > 0 && (
                <circle
                  cx={size / 2}
                  cy={size / 2}
                  r={radius}
                  fill="transparent"
                  stroke="#f43f5e"
                  strokeWidth={strokeWidth}
                  strokeDasharray={`${hardArc} ${circumference - hardArc}`}
                  strokeDashoffset={hardOffset}
                  strokeLinecap="butt"
                  className="transition-all duration-700 ease-out"
                />
              )}
            </svg>

            {/* Centered Solved Count & Label */}
            <div className="absolute inset-0 flex flex-col items-center justify-center text-center pointer-events-none select-none">
              <div className="flex items-baseline justify-center">
                <span className="text-3xl font-extrabold tracking-tight text-white">
                  {solvedCount}
                </span>
                <span className="ml-0.5 text-xs font-semibold text-slate-400">
                  /{totalProblems}
                </span>
              </div>
              <div className="mt-1 flex items-center justify-center gap-1 text-[11px] font-semibold text-emerald-400">
                <svg className="h-3.5 w-3.5 flex-shrink-0" viewBox="0 0 20 20" fill="currentColor">
                  <path
                    fillRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    clipRule="evenodd"
                  />
                </svg>
                <span>Solved</span>
              </div>
            </div>
          </div>

          {/* Attempting count indicator underneath */}
          <div className="mt-3 flex items-center justify-center gap-1.5 text-xs font-medium text-slate-400">
            <span className="h-1.5 w-1.5 rounded-full bg-amber-400" />
            <span>{attemptingCount} Attempting</span>
          </div>
        </div>

        {/* Right Side: Vertically Stacked Difficulty Summaries */}
        <div className="flex flex-col gap-2.5 flex-1 max-w-sm w-full">
          {/* Easy Summary */}
          <div className="rounded-xl border border-cyan-500/15 bg-cyan-500/[0.04] px-4 py-2.5 transition-all hover:bg-cyan-500/[0.07] hover:border-cyan-500/30">
            <div className="flex items-center justify-between text-xs mb-1.5">
              <span className="font-semibold text-cyan-400">Easy</span>
              <div className="text-xs">
                <span className="font-bold text-white">{easySolved}</span>
                <span className="text-slate-400">/{easyTotal}</span>
              </div>
            </div>
            <div className="h-1.5 w-full overflow-hidden rounded-full bg-slate-800/80">
              <div
                className="h-full rounded-full bg-cyan-400 transition-all duration-500"
                style={{ width: `${easyPercent}%` }}
              />
            </div>
          </div>

          {/* Medium Summary */}
          <div className="rounded-xl border border-amber-500/15 bg-amber-500/[0.04] px-4 py-2.5 transition-all hover:bg-amber-500/[0.07] hover:border-amber-500/30">
            <div className="flex items-center justify-between text-xs mb-1.5">
              <span className="font-semibold text-amber-400">Medium</span>
              <div className="text-xs">
                <span className="font-bold text-white">{mediumSolved}</span>
                <span className="text-slate-400">/{mediumTotal}</span>
              </div>
            </div>
            <div className="h-1.5 w-full overflow-hidden rounded-full bg-slate-800/80">
              <div
                className="h-full rounded-full bg-amber-400 transition-all duration-500"
                style={{ width: `${mediumPercent}%` }}
              />
            </div>
          </div>

          {/* Hard Summary */}
          <div className="rounded-xl border border-rose-500/15 bg-rose-500/[0.04] px-4 py-2.5 transition-all hover:bg-rose-500/[0.07] hover:border-rose-500/30">
            <div className="flex items-center justify-between text-xs mb-1.5">
              <span className="font-semibold text-rose-400">Hard</span>
              <div className="text-xs">
                <span className="font-bold text-white">{hardSolved}</span>
                <span className="text-slate-400">/{hardTotal}</span>
              </div>
            </div>
            <div className="h-1.5 w-full overflow-hidden rounded-full bg-slate-800/80">
              <div
                className="h-full rounded-full bg-rose-400 transition-all duration-500"
                style={{ width: `${hardPercent}%` }}
              />
            </div>
          </div>
        </div>

        {/* Topic Breakdown (if available) */}
        {userProgress?.topicStats && userProgress.topicStats.length > 0 && (
          <div className="border-t border-slate-800/80 pt-4 lg:border-t-0 lg:border-l lg:pl-6 lg:pt-0 lg:max-w-xs w-full">
            <div className="text-[11px] font-semibold uppercase tracking-wider text-slate-400 mb-2">
              Topics
            </div>
            <div className="flex flex-wrap gap-1.5 max-h-32 overflow-y-auto pr-1 scrollbar-thin">
              {userProgress.topicStats.map((topic) => (
                <span
                  key={topic.name}
                  className={`inline-flex items-center gap-1.5 rounded-lg px-2 py-1 text-xs font-medium border ${
                    topic.solved > 0
                      ? 'border-cyan-500/30 bg-cyan-500/10 text-cyan-300'
                      : 'border-slate-800 bg-slate-950/40 text-slate-400'
                  }`}
                >
                  <span>{topic.name}</span>
                  <span className="rounded bg-slate-900/80 px-1 py-0.2 text-[10px] text-slate-300">
                    {topic.solved}/{topic.total}
                  </span>
                </span>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DsaProgressCard;
