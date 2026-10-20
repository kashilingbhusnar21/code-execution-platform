import React, { useEffect, useRef, useState } from 'react';

// Lightweight dynamic loader for Mermaid diagrams
let mermaidPromise = null;
function getMermaid() {
  if (!mermaidPromise && typeof window !== 'undefined') {
    mermaidPromise = import(/* @vite-ignore */ 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs')
      .then((m) => {
        const mermaid = m.default || m;
        mermaid.initialize({
          startOnLoad: false,
          theme: 'dark',
          themeVariables: {
            darkMode: true,
            background: '#0f172a',
            primaryColor: '#0284c7',
            primaryTextColor: '#f8fafc',
            primaryBorderColor: '#38bdf8',
            lineColor: '#64748b',
            secondaryColor: '#1e293b',
            tertiaryColor: '#0f172a',
          },
        });
        return mermaid;
      })
      .catch((err) => {
        console.warn('Could not load Mermaid from CDN, using code block fallback:', err);
        return null;
      });
  }
  return mermaidPromise;
}

const MermaidBlock = ({ chart, id }) => {
  const containerRef = useRef(null);
  const [svgHtml, setSvgHtml] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    getMermaid()
      .then(async (mermaid) => {
        if (!isMounted) return;
        if (!mermaid) {
          setLoading(false);
          return;
        }
        try {
          const uniqueId = `mermaid-${id}-${Math.random().toString(36).substr(2, 9)}`;
          const { svg } = await mermaid.render(uniqueId, chart.trim());
          if (isMounted) {
            setSvgHtml(svg);
            setLoading(false);
          }
        } catch (err) {
          console.warn('Mermaid rendering error:', err);
          if (isMounted) setLoading(false);
        }
      })
      .catch(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, [chart, id]);

  if (svgHtml) {
    return (
      <div className="my-6 overflow-x-auto rounded-2xl border border-sky-500/20 bg-slate-950/70 p-6 shadow-xl backdrop-blur-md">
        <div className="mb-2 flex items-center justify-between text-xs font-semibold uppercase tracking-wider text-sky-400">
          <span className="flex items-center gap-1.5">
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polygon points="12 2 2 7 12 12 22 7 12 2" />
              <polyline points="2 17 12 22 22 17" />
              <polyline points="2 12 12 17 22 12" />
            </svg>
            Architecture Flow
          </span>
          <span className="text-slate-500 text-[10px]">Interactive Diagram</span>
        </div>
        <div
          ref={containerRef}
          className="flex justify-center [&>svg]:max-w-full [&>svg]:h-auto"
          dangerouslySetInnerHTML={{ __html: svgHtml }}
        />
      </div>
    );
  }

  return (
    <div className="my-6 rounded-2xl border border-slate-800 bg-slate-950/90 p-4 font-mono text-xs text-sky-300">
      <div className="mb-2 text-xs font-semibold text-slate-400">Mermaid Diagram</div>
      <pre className="overflow-x-auto">{chart}</pre>
    </div>
  );
};

const CodeBlock = ({ language, code }) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="my-5 overflow-hidden rounded-xl border border-slate-800 bg-[#070d18] shadow-lg">
      <div className="flex items-center justify-between border-b border-slate-800/80 bg-slate-900/80 px-4 py-2 text-xs text-slate-400">
        <span className="font-mono uppercase font-semibold text-sky-400">{language || 'text'}</span>
        <button
          onClick={handleCopy}
          className="flex items-center gap-1 rounded-md border border-slate-700/60 bg-slate-800/60 px-2.5 py-1 text-[11px] font-medium text-slate-300 hover:bg-slate-700 hover:text-white transition-all"
        >
          {copied ? (
            <>
              <svg className="h-3.5 w-3.5 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <polyline points="20 6 9 17 4 12" />
              </svg>
              <span className="text-emerald-400">Copied!</span>
            </>
          ) : (
            <>
              <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
              </svg>
              <span>Copy</span>
            </>
          )}
        </button>
      </div>
      <pre className="overflow-x-auto p-4 font-mono text-xs sm:text-sm leading-relaxed text-slate-200">
        <code>{code}</code>
      </pre>
    </div>
  );
};

export default function MarkdownViewer({ content }) {
  if (!content) {
    return (
      <div className="flex min-h-[300px] items-center justify-center text-slate-500">
        No content available for this lesson.
      </div>
    );
  }

  // Parse lines into tokens
  const lines = content.split('\n');
  const elements = [];
  let i = 0;
  let keyIndex = 0;

  while (i < lines.length) {
    const line = lines[i];

    // Fenced Code Block / Mermaid
    if (line.trim().startsWith('```')) {
      const language = line.trim().replace(/^```/, '').trim();
      const codeLines = [];
      i++;
      while (i < lines.length && !lines[i].trim().startsWith('```')) {
        codeLines.push(lines[i]);
        i++;
      }
      i++; // skip closing ```
      const rawCode = codeLines.join('\n');

      if (language === 'mermaid') {
        elements.push(<MermaidBlock key={`el-${keyIndex++}`} chart={rawCode} id={keyIndex} />);
      } else {
        elements.push(<CodeBlock key={`el-${keyIndex++}`} language={language} code={rawCode} />);
      }
      continue;
    }

    // Callout alerts: > [!NOTE], > [!WARNING], > [!IMPORTANT], > [!TIP]
    if (line.trim().startsWith('> [!')) {
      const match = line.trim().match(/^>\s*\[!(NOTE|WARNING|IMPORTANT|TIP|CAUTION)\]\s*(.*)$/i);
      const alertType = match ? match[1].toUpperCase() : 'NOTE';
      const firstLineText = match ? match[2] : '';
      const alertLines = firstLineText ? [firstLineText] : [];
      i++;
      while (i < lines.length && lines[i].trim().startsWith('>')) {
        alertLines.push(lines[i].replace(/^>\s?/, ''));
        i++;
      }

      const alertStyles = {
        NOTE: 'border-sky-500/40 bg-sky-500/10 text-sky-200',
        TIP: 'border-emerald-500/40 bg-emerald-500/10 text-emerald-200',
        WARNING: 'border-amber-500/40 bg-amber-500/10 text-amber-200',
        IMPORTANT: 'border-indigo-500/40 bg-indigo-500/10 text-indigo-200',
        CAUTION: 'border-rose-500/40 bg-rose-500/10 text-rose-200',
      };

      elements.push(
        <div
          key={`el-${keyIndex++}`}
          className={`my-5 rounded-xl border p-4 text-sm leading-relaxed backdrop-blur-sm ${alertStyles[alertType] || alertStyles.NOTE}`}
        >
          <div className="mb-1.5 flex items-center gap-2 font-bold tracking-wide">
            <span className="rounded bg-black/30 px-2 py-0.5 text-xs font-mono">{alertType}</span>
          </div>
          <div className="space-y-1">{alertLines.map((al, idx) => <p key={idx}>{al}</p>)}</div>
        </div>
      );
      continue;
    }

    // Standard Blockquote
    if (line.trim().startsWith('>')) {
      const bqLines = [];
      while (i < lines.length && lines[i].trim().startsWith('>')) {
        bqLines.push(lines[i].replace(/^>\s?/, ''));
        i++;
      }
      elements.push(
        <blockquote
          key={`el-${keyIndex++}`}
          className="my-4 border-l-4 border-sky-500/60 bg-slate-900/40 py-2.5 px-4 italic text-slate-300 rounded-r-xl"
        >
          {bqLines.map((bl, idx) => (
            <p key={idx} className="my-0.5">{bl}</p>
          ))}
        </blockquote>
      );
      continue;
    }

    // Headings
    if (line.startsWith('# ')) {
      elements.push(
        <h1 key={`el-${keyIndex++}`} className="mt-8 mb-4 text-2.5xl sm:text-3xl font-extrabold tracking-tight text-white">
          {line.replace('# ', '')}
        </h1>
      );
      i++;
      continue;
    }
    if (line.startsWith('## ')) {
      elements.push(
        <h2 key={`el-${keyIndex++}`} className="mt-8 mb-3 text-xl sm:text-2xl font-bold tracking-tight text-slate-100 border-b border-slate-800 pb-2">
          {line.replace('## ', '')}
        </h2>
      );
      i++;
      continue;
    }
    if (line.startsWith('### ')) {
      elements.push(
        <h3 key={`el-${keyIndex++}`} className="mt-6 mb-2 text-lg sm:text-xl font-semibold text-sky-300">
          {line.replace('### ', '')}
        </h3>
      );
      i++;
      continue;
    }

    // Horizontal Rule
    if (line.trim() === '---' || line.trim() === '***') {
      elements.push(<hr key={`el-${keyIndex++}`} className="my-8 border-slate-800" />);
      i++;
      continue;
    }

    // Interactive Quiz / Task checklist: - [ ] or - [x]
    if (line.trim().startsWith('- [ ]') || line.trim().startsWith('- [x]')) {
      const isChecked = line.trim().startsWith('- [x]');
      const taskText = line.replace(/-\s*\[[ xX]\]\s*/, '');
      elements.push(
        <div
          key={`el-${keyIndex++}`}
          className="my-1.5 flex items-start gap-3 rounded-lg border border-slate-800/80 bg-slate-900/30 p-2.5 text-sm text-slate-200 transition-colors hover:bg-slate-900/60"
        >
          <input
            type="checkbox"
            defaultChecked={isChecked}
            className="mt-1 h-4 w-4 rounded border-slate-700 bg-slate-800 text-sky-500 focus:ring-sky-500"
          />
          <span className="leading-snug">{taskText}</span>
        </div>
      );
      i++;
      continue;
    }

    // Bulleted List: - item or * item
    if (line.trim().startsWith('- ') || line.trim().startsWith('* ')) {
      const listItems = [];
      while (i < lines.length && (lines[i].trim().startsWith('- ') || lines[i].trim().startsWith('* '))) {
        listItems.push(lines[i].trim().replace(/^[-*]\s+/, ''));
        i++;
      }
      elements.push(
        <ul key={`el-${keyIndex++}`} className="my-3 ml-5 list-disc space-y-1.5 text-slate-300 text-sm leading-relaxed">
          {listItems.map((item, idx) => (
            <li key={idx} dangerouslySetInnerHTML={{ __html: formatInline(item) }} />
          ))}
        </ul>
      );
      continue;
    }

    // Empty Line
    if (!line.trim()) {
      i++;
      continue;
    }

    // Standard Paragraph
    elements.push(
      <p
        key={`el-${keyIndex++}`}
        className="my-3 text-sm sm:text-base leading-relaxed text-slate-300"
        dangerouslySetInnerHTML={{ __html: formatInline(line) }}
      />
    );
    i++;
  }

  return <div className="space-y-1 text-slate-200">{elements}</div>;
}

// Helper to format inline bold, italic, code, links
function formatInline(text) {
  if (!text) return '';
  return text
    // Inline code: `code`
    .replace(/`([^`]+)`/g, '<code class="rounded bg-slate-800/80 px-1.5 py-0.5 font-mono text-xs text-sky-300 border border-slate-700/50">$1</code>')
    // Bold: **text**
    .replace(/\*\*([^*]+)\*\*/g, '<strong class="font-semibold text-white">$1</strong>')
    // Italic: *text*
    .replace(/\*([^*]+)\*/g, '<em class="italic text-slate-200">$1</em>')
    // Links: [text](url)
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" class="text-sky-400 underline underline-offset-4 hover:text-sky-300">$1</a>');
}
