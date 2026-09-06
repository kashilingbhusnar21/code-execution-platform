import React from 'react';
import Editor from '@monaco-editor/react';

export const LANGUAGE_TEMPLATES = {
  java: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}`,
  python: `print("Hello World")`,
  cpp: `#include <iostream>
using namespace std;

int main() {
    cout << "Hello World" << endl;
    return 0;
}`,
};

const MONACO_LANGUAGE_MAP = {
  java: 'java',
  python: 'python',
  cpp: 'cpp',
};

const CodeEditor = ({
  code,
  setCode,
  language,
  setLanguage,
  input,
  setInput,
  onRun,
  isRunning,
}) => {
  const handleLanguageChange = (nextLanguage) => {
    setLanguage(nextLanguage);
    setCode(LANGUAGE_TEMPLATES[nextLanguage] || '');
  };

  const handleReset = () => {
    setCode(LANGUAGE_TEMPLATES[language] || '');
  };

  const monacoLanguage = MONACO_LANGUAGE_MAP[language] || 'java';

  return (
    <div className="code-editor">
      <div className="editor-header">
        <h3>Code Editor</h3>
        <select
          value={language}
          onChange={(e) => handleLanguageChange(e.target.value)}
          className="language-selector"
          disabled={isRunning}
        >
          <option value="java">Java</option>
          <option value="python">Python</option>
          <option value="cpp">C++</option>
        </select>
      </div>

      <div className={`monaco-wrapper${isRunning ? ' monaco-disabled' : ''}`}>
        <Editor
          height="400px"
          theme="vs-dark"
          language={monacoLanguage}
          value={code}
          onChange={(value) => setCode(value ?? '')}
          options={{
            fontSize: 14,
            minimap: { enabled: false },
            automaticLayout: true,
            scrollBeyondLastLine: false,
            tabSize: 4,
            insertSpaces: true,
            autoIndent: 'full',
            formatOnType: true,
            formatOnPaste: true,
            readOnly: isRunning,
            wordWrap: 'on',
            padding: { top: 12, bottom: 12 },
          }}
          loading={<div className="monaco-loading">Loading editor...</div>}
        />
      </div>

      <div className="editor-actions">
        <button
          type="button"
          onClick={handleReset}
          className="btn btn-secondary"
          disabled={isRunning}
        >
          Reset
        </button>
        <button
          type="button"
          onClick={onRun}
          className="btn btn-primary btn-run"
          disabled={isRunning}
        >
          {isRunning ? (
            <>
              <span className="spinner spinner-sm" aria-hidden="true" />
              Running...
            </>
          ) : (
            'Run Code'
          )}
        </button>
      </div>

      <div className="input-section">
        <h4>Input (stdin)</h4>
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          className="input-textarea"
          placeholder={'Example:\n5\n10'}
          spellCheck="false"
          disabled={isRunning}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
