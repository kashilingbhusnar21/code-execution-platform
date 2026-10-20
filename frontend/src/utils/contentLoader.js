// Dynamically loads markdown content files located in /src/content/tracks/
const markdownFiles = import.meta.glob('/src/content/tracks/**/*.md', {
  query: '?raw',
  import: 'default',
  eager: true,
});

/**
 * Loads markdown content based on contentPath stored in the database.
 * E.g. "tracks/java-backend/01-core/01-jvm-architecture.md"
 */
export function getTopicMarkdown(contentPath) {
  if (!contentPath) return null;

  // Try direct match with /src/content/ prefix
  const targetKey = `/src/content/${contentPath.replace(/^\/+/, '')}`;
  if (markdownFiles[targetKey]) {
    return markdownFiles[targetKey];
  }

  // Search by normalized suffix
  const normalizedSearch = contentPath.replace(/\\/g, '/');
  const matchedKey = Object.keys(markdownFiles).find((key) =>
    key.replace(/\\/g, '/').endsWith(normalizedSearch)
  );

  return matchedKey ? markdownFiles[matchedKey] : null;
}
