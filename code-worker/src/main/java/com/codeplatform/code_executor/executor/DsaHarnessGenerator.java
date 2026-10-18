package com.codeplatform.code_executor.executor;

/**
 * Generates test-runner entrypoints (Main.java, main.py, main.cpp) for judgeable DSA problems.
 * Decouples problem signature archetypes from specific method names.
 */
public class DsaHarnessGenerator {

    public static String getJavaHarness(DsaProblemType type) {
        return getJavaHarness(type.getDefinition());
    }

    public static String getPythonHarness(DsaProblemType type) {
        return getPythonHarness(type.getDefinition());
    }

    public static String getCppHarness(DsaProblemType type) {
        return getCppHarness(type.getDefinition());
    }

    // =========================================================================
    // JAVA HARNESS GENERATOR
    // =========================================================================

    public static String getJavaHarness(DsaProblemDef def) {
        String method = def.getMethodName();
        DsaArchetype archetype = def.getArchetype();

        StringBuilder sb = new StringBuilder();
        sb.append("import java.io.*;\n");
        sb.append("import java.util.*;\n");
        sb.append("import java.util.regex.*;\n\n");

        // Include standard ListNode and TreeNode classes for Java
        sb.append("class ListNode {\n");
        sb.append("    int val;\n");
        sb.append("    ListNode next;\n");
        sb.append("    ListNode() {}\n");
        sb.append("    ListNode(int val) { this.val = val; }\n");
        sb.append("    ListNode(int val, ListNode next) { this.val = val; this.next = next; }\n");
        sb.append("}\n\n");

        sb.append("class TreeNode {\n");
        sb.append("    int val;\n");
        sb.append("    TreeNode left;\n");
        sb.append("    TreeNode right;\n");
        sb.append("    TreeNode() {}\n");
        sb.append("    TreeNode(int val) { this.val = val; }\n");
        sb.append("    TreeNode(int val, TreeNode left, TreeNode right) {\n");
        sb.append("        this.val = val;\n");
        sb.append("        this.left = left;\n");
        sb.append("        this.right = right;\n");
        sb.append("    }\n");
        sb.append("}\n\n");

        sb.append("public class Main {\n");
        sb.append("    public static void main(String[] args) throws Exception {\n");
        sb.append("        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));\n");
        sb.append("        StringBuilder sb = new StringBuilder();\n");
        sb.append("        String line;\n");
        sb.append("        while ((line = br.readLine()) != null) {\n");
        sb.append("            sb.append(line).append(\"\\n\");\n");
        sb.append("        }\n");
        sb.append("        String input = sb.toString().trim();\n");
        sb.append("        Solution sol = new Solution();\n\n");

        // Generate invocation based on archetype
        switch (archetype) {
            case INT_ARRAY_TARGET_TO_INT_ARRAY:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int target = parseTarget(input);\n");
                sb.append("        int[] res = sol.").append(method).append("(nums, target);\n");
                sb.append("        System.out.println(formatIntArray(res));\n");
                break;

            case INT_ARRAY_TARGET_TO_INT:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int target = parseTarget(input);\n");
                sb.append("        int res = sol.").append(method).append("(nums, target);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_ARRAY_TO_INT:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int res = sol.").append(method).append("(nums);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_ARRAY_TO_BOOL:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(nums);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_ARRAY_TO_INT_ARRAY:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int[] res = sol.").append(method).append("(nums);\n");
                sb.append("        System.out.println(formatIntArray(res));\n");
                break;

            case IN_PLACE_INT_ARRAY:
                sb.append("        int[] nums = parseIntArray(input);\n");
                if (def.isReturnsLength()) {
                    sb.append("        int k = sol.").append(method).append("(nums);\n");
                    sb.append("        int[] slice = Arrays.copyOf(nums, Math.max(0, k));\n");
                    sb.append("        System.out.println(formatIntArray(slice));\n");
                } else {
                    sb.append("        sol.").append(method).append("(nums);\n");
                    sb.append("        System.out.println(formatIntArray(nums));\n");
                }
                break;

            case INT_ARRAY_INT_TO_INT:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        int res = sol.").append(method).append("(nums, k);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_ARRAY_INT_TO_INT_ARRAY:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        int[] res = sol.").append(method).append("(nums, k);\n");
                sb.append("        System.out.println(formatIntArray(res));\n");
                break;

            case STRING_TO_BOOL:
                sb.append("        String s = parseString(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(s);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case STRING_TO_INT:
                sb.append("        String s = parseString(input);\n");
                sb.append("        int res = sol.").append(method).append("(s);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TWO_STRINGS_TO_BOOL:
                sb.append("        String s = parseStringParam(input, \"s\");\n");
                sb.append("        String t = parseStringParam(input, \"t\");\n");
                sb.append("        boolean res = sol.").append(method).append("(s, t);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TWO_STRINGS_TO_INT:
                sb.append("        String s = parseStringParam(input, \"s\");\n");
                sb.append("        String t = parseStringParam(input, \"t\");\n");
                sb.append("        int res = sol.").append(method).append("(s, t);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case STRING_ARRAY_TO_STRING:
                sb.append("        String[] strs = parseStringArray(input);\n");
                sb.append("        String res = sol.").append(method).append("(strs);\n");
                sb.append("        System.out.println(res != null ? res : \"\");\n");
                break;

            case IN_PLACE_CHAR_ARRAY:
                sb.append("        char[] s = parseCharArray(input);\n");
                sb.append("        sol.").append(method).append("(s);\n");
                sb.append("        System.out.println(formatCharArray(s));\n");
                break;

            case INT_TO_INT:
                sb.append("        int n = parseInt(input);\n");
                sb.append("        int res = sol.").append(method).append("(n);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_TO_BOOL:
                sb.append("        int n = parseInt(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(n);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_TO_INT_ARRAY:
                sb.append("        int n = parseInt(input);\n");
                sb.append("        int[] res = sol.").append(method).append("(n);\n");
                sb.append("        System.out.println(formatIntArray(res));\n");
                break;

            case TWO_INTS_TO_INT:
                sb.append("        int m = parseIntParam(input, \"m\");\n");
                sb.append("        int n = parseIntParam(input, \"n\");\n");
                sb.append("        int res = sol.").append(method).append("(m, n);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case LINKED_LIST_TO_LINKED_LIST:
                sb.append("        ListNode head = parseLinkedList(input);\n");
                sb.append("        ListNode res = sol.").append(method).append("(head);\n");
                sb.append("        System.out.println(formatLinkedList(res));\n");
                break;

            case TWO_LINKED_LISTS_TO_LINKED_LIST:
                sb.append("        ListNode l1 = parseLinkedListParam(input, \"list1\", 1);\n");
                sb.append("        ListNode l2 = parseLinkedListParam(input, \"list2\", 2);\n");
                sb.append("        ListNode res = sol.").append(method).append("(l1, l2);\n");
                sb.append("        System.out.println(formatLinkedList(res));\n");
                break;

            case LINKED_LIST_INT_TO_LINKED_LIST:
                sb.append("        ListNode head = parseLinkedList(input);\n");
                sb.append("        int n = parseIntAfterList(input);\n");
                sb.append("        ListNode res = sol.").append(method).append("(head, n);\n");
                sb.append("        System.out.println(formatLinkedList(res));\n");
                break;

            case IN_PLACE_LINKED_LIST:
                sb.append("        ListNode head = parseLinkedList(input);\n");
                sb.append("        sol.").append(method).append("(head);\n");
                sb.append("        System.out.println(formatLinkedList(head));\n");
                break;

            case LINKED_LIST_DELETE_NODE:
                sb.append("        ListNode head = parseLinkedList(input);\n");
                sb.append("        int targetVal = parseIntAfterList(input);\n");
                sb.append("        ListNode curr = head;\n");
                sb.append("        while (curr != null && curr.val != targetVal) curr = curr.next;\n");
                sb.append("        if (curr != null) sol.").append(method).append("(curr);\n");
                sb.append("        System.out.println(formatLinkedList(head));\n");
                break;

            case LINKED_LIST_ARRAY_TO_LINKED_LIST:
                sb.append("        ListNode[] lists = parseLinkedListArray(input);\n");
                sb.append("        ListNode res = sol.").append(method).append("(lists);\n");
                sb.append("        System.out.println(formatLinkedList(res));\n");
                break;

            case TREE_TO_INT:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        int res = sol.").append(method).append("(root);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TREE_TO_BOOL:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(root);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TREE_TO_TREE:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        TreeNode res = sol.").append(method).append("(root);\n");
                sb.append("        System.out.println(formatTree(res));\n");
                break;

            case TREE_TO_INT_ARRAY:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        List<Integer> res = sol.").append(method).append("(root);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case STRING_INT_TO_INT:
                sb.append("        String s = parseString(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        int res = sol.").append(method).append("(s, k);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_TARGET_INT_ARRAY_TO_INT:
                sb.append("        int target = parseTarget(input);\n");
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int res = sol.").append(method).append("(target, nums);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TWO_STRINGS_TO_INT_ARRAY:
                sb.append("        String s = parseStringParam(input, \"s\");\n");
                sb.append("        String p = parseStringParam(input, \"p\");\n");
                sb.append("        List<Integer> res = sol.").append(method).append("(s, p);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case INT_ARRAY_INT_TO_DOUBLE:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        double res = sol.").append(method).append("(nums, k);\n");
                sb.append("        System.out.println(String.format(java.util.Locale.US, \"%.5f\", res));\n");
                break;

            case TWO_STRINGS_TO_STRING:
                sb.append("        String s = parseStringParam(input, \"s\");\n");
                sb.append("        String t = parseStringParam(input, \"t\");\n");
                sb.append("        String res = sol.").append(method).append("(s, t);\n");
                sb.append("        System.out.println(res != null ? res : \"\");\n");
                break;

            case TWO_TREES_TO_BOOL:
                sb.append("        TreeNode p = parseTreeParam(input, 1);\n");
                sb.append("        TreeNode q = parseTreeParam(input, 2);\n");
                sb.append("        boolean res = sol.").append(method).append("(p, q);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TREE_INT_TO_BOOL:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        int targetSum = parseTarget(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(root, targetSum);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TREE_INT_TO_INT:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        int res = sol.").append(method).append("(root, k);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case TREE_TO_INT_LIST_LIST:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        List<List<Integer>> res = sol.").append(method).append("(root);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case TREE_TWO_NODES_TO_NODE:
                sb.append("        TreeNode root = parseTree(input);\n");
                sb.append("        int pVal = parseIntParam(input, \"p\");\n");
                sb.append("        int qVal = parseIntParam(input, \"q\");\n");
                sb.append("        TreeNode p = findNode(root, pVal);\n");
                sb.append("        TreeNode q = findNode(root, qVal);\n");
                sb.append("        TreeNode res = sol.").append(method).append("(root, p, q);\n");
                sb.append("        System.out.println(res != null ? res.val : \"null\");\n");
                break;

            case MATRIX_INT_TO_MATRIX:
                sb.append("        int[][] points = parseMatrix(input);\n");
                sb.append("        int k = parseTarget(input);\n");
                sb.append("        int[][] res = sol.").append(method).append("(points, k);\n");
                sb.append("        System.out.println(formatMatrix(res));\n");
                break;

            case CHAR_ARRAY_INT_TO_INT:
                sb.append("        char[] tasks = parseCharArray(input);\n");
                sb.append("        int n = parseInt(input);\n");
                sb.append("        int res = sol.").append(method).append("(tasks, n);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case STRING_TO_STRING:
                sb.append("        String s = parseString(input);\n");
                sb.append("        String res = sol.").append(method).append("(s);\n");
                sb.append("        System.out.println(res != null ? res : \"\");\n");
                break;

            case STRING_LIST_TO_BOOL:
                sb.append("        String s = parseString(input);\n");
                sb.append("        List<String> wordDict = parseStringList(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(s, wordDict);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case CHAR_MATRIX_TO_INT:
                sb.append("        char[][] grid = parseCharMatrix(input);\n");
                sb.append("        int res = sol.").append(method).append("(grid);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_MATRIX_TO_INT:
                sb.append("        int[][] grid = parseMatrix(input);\n");
                sb.append("        int res = sol.").append(method).append("(grid);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case MATRIX_INT_INT_INT_TO_MATRIX:
                sb.append("        int[][] image = parseMatrix(input);\n");
                sb.append("        int sr = parseIntParam(input, \"sr\");\n");
                sb.append("        int sc = parseIntParam(input, \"sc\");\n");
                sb.append("        int color = parseIntParam(input, \"color\");\n");
                sb.append("        int[][] res = sol.").append(method).append("(image, sr, sc, color);\n");
                sb.append("        System.out.println(formatMatrix(res));\n");
                break;

            case INT_MATRIX_TO_BOOL:
                sb.append("        int numCourses = parseTarget(input);\n");
                sb.append("        int[][] prerequisites = parseMatrix(input);\n");
                sb.append("        boolean res = sol.").append(method).append("(numCourses, prerequisites);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_MATRIX_TO_INT_ARRAY:
                sb.append("        int numCourses = parseTarget(input);\n");
                sb.append("        int[][] prerequisites = parseMatrix(input);\n");
                sb.append("        int[] res = sol.").append(method).append("(numCourses, prerequisites);\n");
                sb.append("        System.out.println(formatIntArray(res));\n");
                break;

            case INT_MATRIX_TO_LIST_LIST:
                sb.append("        int[][] heights = parseMatrix(input);\n");
                sb.append("        List<List<Integer>> res = sol.").append(method).append("(heights);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case IN_PLACE_CHAR_MATRIX:
                sb.append("        char[][] board = parseCharMatrix(input);\n");
                sb.append("        sol.").append(method).append("(board);\n");
                sb.append("        System.out.println(formatCharMatrix(board));\n");
                break;

            case TWO_STRINGS_LIST_TO_INT:
                sb.append("        String beginWord = parseStringParam(input, \"beginWord\");\n");
                sb.append("        String endWord = parseStringParam(input, \"endWord\");\n");
                sb.append("        List<String> wordList = parseStringList(input);\n");
                sb.append("        int res = sol.").append(method).append("(beginWord, endWord, wordList);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case INT_ARRAY_TO_LIST_LIST:
                sb.append("        int[] nums = parseIntArray(input);\n");
                sb.append("        List<List<Integer>> res = sol.").append(method).append("(nums);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case INT_ARRAY_TARGET_TO_LIST_LIST:
                sb.append("        int[] candidates = parseIntArray(input);\n");
                sb.append("        int target = parseTarget(input);\n");
                sb.append("        List<List<Integer>> res = sol.").append(method).append("(candidates, target);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case INT_TO_STRING_LIST:
                sb.append("        int n = parseInt(input);\n");
                sb.append("        List<String> res = sol.").append(method).append("(n);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case CHAR_MATRIX_STRING_TO_BOOL:
                sb.append("        char[][] board = parseCharMatrix(input);\n");
                sb.append("        String word = parseStringParam(input, \"word\");\n");
                sb.append("        boolean res = sol.").append(method).append("(board, word);\n");
                sb.append("        System.out.println(res);\n");
                break;

            case STRING_TO_STRING_LIST:
                sb.append("        String digits = parseString(input);\n");
                sb.append("        List<String> res = sol.").append(method).append("(digits);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case STRING_TO_LIST_LIST_STRING:
                sb.append("        String s = parseString(input);\n");
                sb.append("        List<List<String>> res = sol.").append(method).append("(s);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case INT_TO_LIST_LIST_STRING:
                sb.append("        int n = parseInt(input);\n");
                sb.append("        List<List<String>> res = sol.").append(method).append("(n);\n");
                sb.append("        System.out.println(res != null ? res.toString() : \"[]\");\n");
                break;

            case TWO_INT_ARRAYS_TO_DOUBLE:
                sb.append("        int[] nums1 = parseIntArrayParam(input, 1);\n");
                sb.append("        int[] nums2 = parseIntArrayParam(input, 2);\n");
                sb.append("        double res = sol.").append(method).append("(nums1, nums2);\n");
                sb.append("        System.out.println(String.format(java.util.Locale.US, \"%.5f\", res));\n");
                break;
        }

        sb.append("    }\n\n");

        // Helper parsers & formatters
        appendJavaHelpers(sb);

        sb.append("}\n");
        return sb.toString();
    }

    private static void appendJavaHelpers(StringBuilder sb) {
        sb.append("    private static int[] parseIntArray(String text) {\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        int end = text.indexOf(']', start);\n");
        sb.append("        if (start == -1 || end == -1) return new int[0];\n");
        sb.append("        String inside = text.substring(start + 1, end).trim();\n");
        sb.append("        if (inside.isEmpty()) return new int[0];\n");
        sb.append("        String[] parts = inside.split(\"[,\\\\s]+\");\n");
        sb.append("        List<Integer> list = new ArrayList<>();\n");
        sb.append("        for (String p : parts) {\n");
        sb.append("            String clean = p.trim();\n");
        sb.append("            if (!clean.isEmpty()) list.add(Integer.parseInt(clean));\n");
        sb.append("        }\n");
        sb.append("        int[] arr = new int[list.size()];\n");
        sb.append("        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);\n");
        sb.append("        return arr;\n");
        sb.append("    }\n\n");

        sb.append("    private static int parseTarget(String text) {\n");
        sb.append("        Matcher m = Pattern.compile(\"(?:target|k)\\\\s*=\\\\s*(-?\\\\d+)\").matcher(text);\n");
        sb.append("        if (m.find()) return Integer.parseInt(m.group(1));\n");
        sb.append("        int end = text.indexOf(']');\n");
        sb.append("        if (end != -1) {\n");
        sb.append("            Matcher m2 = Pattern.compile(\"(-?\\\\d+)\").matcher(text.substring(end + 1));\n");
        sb.append("            if (m2.find()) return Integer.parseInt(m2.group(1));\n");
        sb.append("        }\n");
        sb.append("        return 0;\n");
        sb.append("    }\n\n");

        sb.append("    private static int parseInt(String text) {\n");
        sb.append("        Matcher m = Pattern.compile(\"(?:n|x|val)\\\\s*=\\\\s*(-?\\\\d+)\").matcher(text);\n");
        sb.append("        if (m.find()) return Integer.parseInt(m.group(1));\n");
        sb.append("        Matcher m2 = Pattern.compile(\"(-?\\\\d+)\").matcher(text);\n");
        sb.append("        if (m2.find()) return Integer.parseInt(m2.group(1));\n");
        sb.append("        return 0;\n");
        sb.append("    }\n\n");

        sb.append("    private static int parseIntParam(String text, String paramName) {\n");
        sb.append("        Matcher m = Pattern.compile(\"\\\\b\" + paramName + \"\\\\s*=\\\\s*(-?\\\\d+)\").matcher(text);\n");
        sb.append("        if (m.find()) return Integer.parseInt(m.group(1));\n");
        sb.append("        return 0;\n");
        sb.append("    }\n\n");

        sb.append("    private static char[] parseCharArray(String text) {\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        int end = text.lastIndexOf(']');\n");
        sb.append("        if (start != -1 && end != -1) {\n");
        sb.append("            String inside = text.substring(start + 1, end);\n");
        sb.append("            List<Character> list = new ArrayList<>();\n");
        sb.append("            Matcher m = Pattern.compile(\"[\\\"']([^\\\"']+)[\\\"']|([a-zA-Z0-9])\").matcher(inside);\n");
        sb.append("            while (m.find()) {\n");
        sb.append("                String val = m.group(1) != null ? m.group(1) : m.group(2);\n");
        sb.append("                if (!val.isEmpty()) list.add(val.charAt(0));\n");
        sb.append("            }\n");
        sb.append("            char[] arr = new char[list.size()];\n");
        sb.append("            for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);\n");
        sb.append("            return arr;\n");
        sb.append("        }\n");
        sb.append("        Matcher m = Pattern.compile(\"s\\\\s*=\\\\s*\\\"([^\\\"]*)\\\"|\\\"([^\\\"]*)\\\"\").matcher(text);\n");
        sb.append("        if (m.find()) {\n");
        sb.append("            String val = m.group(1) != null ? m.group(1) : m.group(2);\n");
        sb.append("            return val.toCharArray();\n");
        sb.append("        }\n");
        sb.append("        return text.trim().toCharArray();\n");
        sb.append("    }\n\n");

        sb.append("    private static String parseString(String text) {\n");
        sb.append("        Matcher m = Pattern.compile(\"s\\\\s*=\\\\s*\\\"([^\\\"]*)\\\"|\\\"([^\\\"]*)\\\"\").matcher(text);\n");
        sb.append("        if (m.find()) {\n");
        sb.append("            return m.group(1) != null ? m.group(1) : m.group(2);\n");
        sb.append("        }\n");
        sb.append("        return text.replaceFirst(\"^s\\\\s*=\\\\s*\", \"\").trim();\n");
        sb.append("    }\n\n");

        sb.append("    private static String parseStringParam(String text, String paramName) {\n");
        sb.append("        Matcher m = Pattern.compile(\"\\\\b\" + paramName + \"\\\\s*=\\\\s*\\\"([^\\\"]*)\\\"\").matcher(text);\n");
        sb.append("        if (m.find()) return m.group(1);\n");
        sb.append("        Matcher m2 = Pattern.compile(\"\\\"([^\\\"]*)\\\"\").matcher(text);\n");
        sb.append("        if (\"t\".equals(paramName) && m2.find() && m2.find()) {\n");
        sb.append("            return m2.group(1);\n");
        sb.append("        }\n");
        sb.append("        if (m2.find(0)) return m2.group(1);\n");
        sb.append("        return \"\";\n");
        sb.append("    }\n\n");

        sb.append("    private static String[] parseStringArray(String text) {\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        int end = text.lastIndexOf(']');\n");
        sb.append("        if (start == -1 || end == -1) return new String[0];\n");
        sb.append("        String inside = text.substring(start + 1, end);\n");
        sb.append("        List<String> list = new ArrayList<>();\n");
        sb.append("        Matcher m = Pattern.compile(\"[\\\"']([^\\\"']*)[\\\"']\").matcher(inside);\n");
        sb.append("        while (m.find()) list.add(m.group(1));\n");
        sb.append("        return list.toArray(new String[0]);\n");
        sb.append("    }\n\n");

        sb.append("    private static ListNode parseLinkedList(String text) {\n");
        sb.append("        int[] arr = parseIntArray(text);\n");
        sb.append("        ListNode dummy = new ListNode(0);\n");
        sb.append("        ListNode curr = dummy;\n");
        sb.append("        for (int v : arr) {\n");
        sb.append("            curr.next = new ListNode(v);\n");
        sb.append("            curr = curr.next;\n");
        sb.append("        }\n");
        sb.append("        return dummy.next;\n");
        sb.append("    }\n\n");

        sb.append("    private static ListNode parseLinkedListParam(String text, String name, int index) {\n");
        sb.append("        int idx = 0;\n");
        sb.append("        for (int i = 0; i < index; i++) {\n");
        sb.append("            idx = text.indexOf('[', idx);\n");
        sb.append("            if (idx == -1) break;\n");
        sb.append("            if (i < index - 1) idx++;\n");
        sb.append("        }\n");
        sb.append("        if (idx == -1) return parseLinkedList(text);\n");
        sb.append("        return parseLinkedList(text.substring(idx));\n");
        sb.append("    }\n\n");

        sb.append("    private static TreeNode parseTree(String text) {\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        int end = text.lastIndexOf(']');\n");
        sb.append("        if (start == -1 || end == -1) return null;\n");
        sb.append("        String inside = text.substring(start + 1, end).trim();\n");
        sb.append("        if (inside.isEmpty()) return null;\n");
        sb.append("        String[] parts = inside.split(\"[,\\\\s]+\");\n");
        sb.append("        if (parts.length == 0 || \"null\".equalsIgnoreCase(parts[0].trim())) return null;\n");
        sb.append("        TreeNode root = new TreeNode(Integer.parseInt(parts[0].trim()));\n");
        sb.append("        Queue<TreeNode> q = new LinkedList<>();\n");
        sb.append("        q.add(root);\n");
        sb.append("        int i = 1;\n");
        sb.append("        while (!q.isEmpty() && i < parts.length) {\n");
        sb.append("            TreeNode curr = q.poll();\n");
        sb.append("            if (i < parts.length && !\"null\".equalsIgnoreCase(parts[i].trim()) && !parts[i].trim().isEmpty()) {\n");
        sb.append("                curr.left = new TreeNode(Integer.parseInt(parts[i].trim()));\n");
        sb.append("                q.add(curr.left);\n");
        sb.append("            }\n");
        sb.append("            i++;\n");
        sb.append("            if (i < parts.length && !\"null\".equalsIgnoreCase(parts[i].trim()) && !parts[i].trim().isEmpty()) {\n");
        sb.append("                curr.right = new TreeNode(Integer.parseInt(parts[i].trim()));\n");
        sb.append("                q.add(curr.right);\n");
        sb.append("            }\n");
        sb.append("            i++;\n");
        sb.append("        }\n");
        sb.append("        return root;\n");
        sb.append("    }\n\n");

        sb.append("    private static String formatIntArray(int[] arr) {\n");
        sb.append("        if (arr == null) return \"[]\";\n");
        sb.append("        StringBuilder sb = new StringBuilder(\"[\");\n");
        sb.append("        for (int i = 0; i < arr.length; i++) {\n");
        sb.append("            sb.append(arr[i]);\n");
        sb.append("            if (i < arr.length - 1) sb.append(\", \");\n");
        sb.append("        }\n");
        sb.append("        return sb.append(\"]\").toString();\n");
        sb.append("    }\n\n");

        sb.append("    private static String formatCharArray(char[] arr) {\n");
        sb.append("        if (arr == null) return \"[]\";\n");
        sb.append("        StringBuilder sb = new StringBuilder(\"[\");\n");
        sb.append("        for (int i = 0; i < arr.length; i++) {\n");
        sb.append("            sb.append(\"\\\"\").append(arr[i]).append(\"\\\"\");\n");
        sb.append("            if (i < arr.length - 1) sb.append(\", \");\n");
        sb.append("        }\n");
        sb.append("        return sb.append(\"]\").toString();\n");
        sb.append("    }\n\n");

        sb.append("    private static int parseIntAfterList(String text) {\n");
        sb.append("        Matcher m = Pattern.compile(\"(?:n|k|x|val|node|target)\\\\s*=\\\\s*(-?\\\\d+)\").matcher(text);\n");
        sb.append("        if (m.find()) return Integer.parseInt(m.group(1));\n");
        sb.append("        int end = text.lastIndexOf(']');\n");
        sb.append("        if (end != -1) {\n");
        sb.append("            Matcher m2 = Pattern.compile(\"(-?\\\\d+)\").matcher(text.substring(end + 1));\n");
        sb.append("            if (m2.find()) return Integer.parseInt(m2.group(1));\n");
        sb.append("        }\n");
        sb.append("        return 0;\n");
        sb.append("    }\n\n");

        sb.append("    private static ListNode[] parseLinkedListArray(String text) {\n");
        sb.append("        int firstBracket = text.indexOf('[');\n");
        sb.append("        int lastBracket = text.lastIndexOf(']');\n");
        sb.append("        if (firstBracket == -1 || lastBracket == -1 || firstBracket >= lastBracket) return new ListNode[0];\n");
        sb.append("        String inside = text.substring(firstBracket + 1, lastBracket).trim();\n");
        sb.append("        if (inside.isEmpty()) return new ListNode[0];\n");
        sb.append("        List<ListNode> result = new ArrayList<>();\n");
        sb.append("        int i = 0;\n");
        sb.append("        while (i < inside.length()) {\n");
        sb.append("            int start = inside.indexOf('[', i);\n");
        sb.append("            if (start == -1) break;\n");
        sb.append("            int end = inside.indexOf(']', start);\n");
        sb.append("            if (end == -1) break;\n");
        sb.append("            String listStr = inside.substring(start, end + 1);\n");
        sb.append("            result.add(parseLinkedList(listStr));\n");
        sb.append("            i = end + 1;\n");
        sb.append("        }\n");
        sb.append("        return result.toArray(new ListNode[0]);\n");
        sb.append("    }\n\n");

        sb.append("    private static String formatLinkedList(ListNode head) {\n");
        sb.append("        List<Integer> list = new ArrayList<>();\n");
        sb.append("        Set<ListNode> seen = new HashSet<>();\n");
        sb.append("        ListNode curr = head;\n");
        sb.append("        while (curr != null) {\n");
        sb.append("            if (!seen.add(curr)) break;\n");
        sb.append("            list.add(curr.val);\n");
        sb.append("            curr = curr.next;\n");
        sb.append("        }\n");
        sb.append("        return list.toString();\n");
        sb.append("    }\n\n");

        sb.append("    private static String formatTree(TreeNode root) {\n");
        sb.append("        if (root == null) return \"[]\";\n");
        sb.append("        List<String> list = new ArrayList<>();\n");
        sb.append("        Queue<TreeNode> q = new LinkedList<>();\n");
        sb.append("        q.add(root);\n");
        sb.append("        while (!q.isEmpty()) {\n");
        sb.append("            TreeNode curr = q.poll();\n");
        sb.append("            if (curr != null) {\n");
        sb.append("                list.add(String.valueOf(curr.val));\n");
        sb.append("                q.add(curr.left);\n");
        sb.append("                q.add(curr.right);\n");
        sb.append("            } else {\n");
        sb.append("                list.add(\"null\");\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        while (!list.isEmpty() && \"null\".equals(list.get(list.size() - 1))) {\n");
        sb.append("            list.remove(list.size() - 1);\n");
        sb.append("        }\n");
        sb.append("        return list.toString();\n");
        sb.append("    }\n\n");
        sb.append("    private static TreeNode parseTreeParam(String text, int index) {\n");
        sb.append("        int start = -1, depth = 0, found = 0;\n");
        sb.append("        for (int i = 0; i < text.length(); i++) {\n");
        sb.append("            if (text.charAt(i) == '[') {\n");
        sb.append("                if (depth == 0) {\n");
        sb.append("                    found++;\n");
        sb.append("                    if (found == index) start = i;\n");
        sb.append("                }\n");
        sb.append("                depth++;\n");
        sb.append("            } else if (text.charAt(i) == ']') {\n");
        sb.append("                depth--;\n");
        sb.append("                if (depth == 0 && found == index && start != -1) {\n");
        sb.append("                    return parseTree(text.substring(start, i + 1));\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        return null;\n");
        sb.append("    }\n\n");
        sb.append("    private static TreeNode findNode(TreeNode root, int val) {\n");
        sb.append("        if (root == null) return null;\n");
        sb.append("        if (root.val == val) return root;\n");
        sb.append("        TreeNode left = findNode(root.left, val);\n");
        sb.append("        if (left != null) return left;\n");
        sb.append("        return findNode(root.right, val);\n");
        sb.append("    }\n\n");
        sb.append("    private static int[][] parseMatrix(String text) {\n");
        sb.append("        List<int[]> rows = new ArrayList<>();\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        if (start == -1) return new int[0][0];\n");
        sb.append("        int idx = start + 1;\n");
        sb.append("        while ((idx = text.indexOf('[', idx)) != -1) {\n");
        sb.append("            int end = text.indexOf(']', idx);\n");
        sb.append("            if (end == -1) break;\n");
        sb.append("            rows.add(parseIntArray(text.substring(idx, end + 1)));\n");
        sb.append("            idx = end + 1;\n");
        sb.append("        }\n");
        sb.append("        return rows.toArray(new int[rows.size()][]);\n");
        sb.append("    }\n\n");
        sb.append("    private static String formatMatrix(int[][] m) {\n");
        sb.append("        StringBuilder b = new StringBuilder(\"[\");\n");
        sb.append("        for (int i = 0; i < m.length; i++) {\n");
        sb.append("            b.append(formatIntArray(m[i]));\n");
        sb.append("            if (i < m.length - 1) b.append(\", \");\n");
        sb.append("        }\n");
        sb.append("        b.append(\"]\");\n");
        sb.append("        return b.toString();\n");
        sb.append("    }\n\n");
        sb.append("    private static List<String> parseStringList(String text) {\n");
        sb.append("        List<String> list = new ArrayList<>();\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        int end = text.lastIndexOf(']');\n");
        sb.append("        if (start != -1 && end != -1) {\n");
        sb.append("            Matcher m = Pattern.compile(\"\\\"([^\\\"]*)\\\"|'([^']*)'\").matcher(text.substring(start, end + 1));\n");
        sb.append("            while (m.find()) {\n");
        sb.append("                list.add(m.group(1) != null ? m.group(1) : m.group(2));\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        return list;\n");
        sb.append("    }\n\n");
        sb.append("    private static char[][] parseCharMatrix(String text) {\n");
        sb.append("        List<char[]> rows = new ArrayList<>();\n");
        sb.append("        int start = text.indexOf('[');\n");
        sb.append("        if (start == -1) return new char[0][0];\n");
        sb.append("        int idx = start + 1;\n");
        sb.append("        while ((idx = text.indexOf('[', idx)) != -1) {\n");
        sb.append("            int end = text.indexOf(']', idx);\n");
        sb.append("            if (end == -1) break;\n");
        sb.append("            rows.add(parseCharArray(text.substring(idx, end + 1)));\n");
        sb.append("            idx = end + 1;\n");
        sb.append("        }\n");
        sb.append("        return rows.toArray(new char[rows.size()][]);\n");
        sb.append("    }\n\n");
        sb.append("    private static String formatCharMatrix(char[][] m) {\n");
        sb.append("        StringBuilder b = new StringBuilder(\"[\");\n");
        sb.append("        for (int i = 0; i < m.length; i++) {\n");
        sb.append("            b.append(\"[\");\n");
        sb.append("            for (int j = 0; j < m[i].length; j++) {\n");
        sb.append("                b.append(\"\\\"\").append(m[i][j]).append(\"\\\"\");\n");
        sb.append("                if (j < m[i].length - 1) b.append(\", \");\n");
        sb.append("            }\n");
        sb.append("            b.append(\"]\");\n");
        sb.append("            if (i < m.length - 1) b.append(\", \");\n");
        sb.append("        }\n");
        sb.append("        b.append(\"]\");\n");
        sb.append("        return b.toString();\n");
        sb.append("    }\n\n");
        sb.append("    private static int[] parseIntArrayParam(String text, int index) {\n");
        sb.append("        int count = 0, idx = 0;\n");
        sb.append("        while ((idx = text.indexOf('[', idx)) != -1) {\n");
        sb.append("            count++;\n");
        sb.append("            int end = text.indexOf(']', idx);\n");
        sb.append("            if (end == -1) break;\n");
        sb.append("            if (count == index) {\n");
        sb.append("                return parseIntArray(text.substring(idx, end + 1));\n");
        sb.append("            }\n");
        sb.append("            idx = end + 1;\n");
        sb.append("        }\n");
        sb.append("        return new int[0];\n");
        sb.append("    }\n");
    }

    // =========================================================================
    // PYTHON HARNESS GENERATOR
    // =========================================================================

    public static String getPythonHarness(DsaProblemDef def) {
        String method = def.getMethodName();
        DsaArchetype archetype = def.getArchetype();

        StringBuilder sb = new StringBuilder();
        sb.append("import sys\n");
        sb.append("import json\n");
        sb.append("import re\n");
        sb.append("from collections import deque\n");
        sb.append("from typing import List, Dict, Tuple, Set, Optional, Any\n\n");

        // Definitions
        sb.append("class ListNode:\n");
        sb.append("    def __init__(self, val=0, next=None):\n");
        sb.append("        self.val = val\n");
        sb.append("        self.next = next\n\n");

        sb.append("class TreeNode:\n");
        sb.append("    def __init__(self, val=0, left=None, right=None):\n");
        sb.append("        self.val = val\n");
        sb.append("        self.left = left\n");
        sb.append("        self.right = right\n\n");

        sb.append("import solution\n");
        sb.append("from solution import Solution\n\n");

        sb.append("raw_input = sys.stdin.read().strip()\n\n");

        appendPythonHelpers(sb);

        sb.append("sol = Solution()\n\n");

        switch (archetype) {
            case INT_ARRAY_TARGET_TO_INT_ARRAY:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("target = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums, target)\n");
                sb.append("print(json.dumps(res))\n");
                break;

            case INT_ARRAY_TARGET_TO_INT:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("target = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums, target)\n");
                sb.append("print(res)\n");
                break;

            case INT_ARRAY_TO_INT:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums)\n");
                sb.append("print(res)\n");
                break;

            case INT_ARRAY_TO_BOOL:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums)\n");
                sb.append("print(\"true\" if res else \"false\")\n");
                break;

            case INT_ARRAY_TO_INT_ARRAY:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums)\n");
                sb.append("print(json.dumps(res))\n");
                break;

            case IN_PLACE_INT_ARRAY:
                sb.append("nums = parse_int_array(raw_input)\n");
                if (def.isReturnsLength()) {
                    sb.append("k = sol.").append(method).append("(nums)\n");
                    sb.append("print(json.dumps(nums[:k]))\n");
                } else {
                    sb.append("sol.").append(method).append("(nums)\n");
                    sb.append("print(json.dumps(nums))\n");
                }
                break;

            case INT_ARRAY_INT_TO_INT:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums, k)\n");
                sb.append("print(res)\n");
                break;

            case INT_ARRAY_INT_TO_INT_ARRAY:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums, k)\n");
                sb.append("if res is None: res = nums\n");
                sb.append("print(json.dumps(res))\n");
                break;

            case STRING_TO_BOOL:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s)\n");
                sb.append("print(\"true\" if res else \"false\")\n");
                break;

            case STRING_TO_INT:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s)\n");
                sb.append("print(res)\n");
                break;

            case TWO_STRINGS_TO_BOOL:
                sb.append("s = parse_string_param(raw_input, 's')\n");
                sb.append("t = parse_string_param(raw_input, 't')\n");
                sb.append("res = sol.").append(method).append("(s, t)\n");
                sb.append("print(\"true\" if res else \"false\")\n");
                break;

            case TWO_STRINGS_TO_INT:
                sb.append("s = parse_string_param(raw_input, 's')\n");
                sb.append("t = parse_string_param(raw_input, 't')\n");
                sb.append("res = sol.").append(method).append("(s, t)\n");
                sb.append("print(res)\n");
                break;

            case STRING_ARRAY_TO_STRING:
                sb.append("strs = parse_string_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(strs)\n");
                sb.append("print(res if res is not None else \"\")\n");
                break;

            case IN_PLACE_CHAR_ARRAY:
                sb.append("s = parse_char_array(raw_input)\n");
                sb.append("sol.").append(method).append("(s)\n");
                sb.append("print(json.dumps(s))\n");
                break;

            case INT_TO_INT:
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(n)\n");
                sb.append("print(res)\n");
                break;

            case INT_TO_BOOL:
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(n)\n");
                sb.append("print(\"true\" if res else \"false\")\n");
                break;

            case INT_TO_INT_ARRAY:
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(n)\n");
                sb.append("print(json.dumps(res))\n");
                break;

            case TWO_INTS_TO_INT:
                sb.append("m = parse_int_param(raw_input, 'm')\n");
                sb.append("n = parse_int_param(raw_input, 'n')\n");
                sb.append("res = sol.").append(method).append("(m, n)\n");
                sb.append("print(res)\n");
                break;

            case LINKED_LIST_TO_LINKED_LIST:
                sb.append("head = parse_linked_list(raw_input)\n");
                sb.append("res = sol.").append(method).append("(head)\n");
                sb.append("print(format_linked_list(res))\n");
                break;

            case TWO_LINKED_LISTS_TO_LINKED_LIST:
                sb.append("l1 = parse_linked_list_param(raw_input, 1)\n");
                sb.append("l2 = parse_linked_list_param(raw_input, 2)\n");
                sb.append("res = sol.").append(method).append("(l1, l2)\n");
                sb.append("print(format_linked_list(res))\n");
                break;

            case LINKED_LIST_INT_TO_LINKED_LIST:
                sb.append("head = parse_linked_list(raw_input)\n");
                sb.append("n = parse_int_after_list(raw_input)\n");
                sb.append("res = sol.").append(method).append("(head, n)\n");
                sb.append("print(format_linked_list(res))\n");
                break;

            case IN_PLACE_LINKED_LIST:
                sb.append("head = parse_linked_list(raw_input)\n");
                sb.append("sol.").append(method).append("(head)\n");
                sb.append("print(format_linked_list(head))\n");
                break;

            case LINKED_LIST_DELETE_NODE:
                sb.append("head = parse_linked_list(raw_input)\n");
                sb.append("target_val = parse_int_after_list(raw_input)\n");
                sb.append("curr = head\n");
                sb.append("while curr and curr.val != target_val: curr = curr.next\n");
                sb.append("if curr: sol.").append(method).append("(curr)\n");
                sb.append("print(format_linked_list(head))\n");
                break;

            case LINKED_LIST_ARRAY_TO_LINKED_LIST:
                sb.append("lists = parse_linked_list_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(lists)\n");
                sb.append("print(format_linked_list(res))\n");
                break;

            case TREE_TO_INT:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root)\n");
                sb.append("print(res)\n");
                break;

            case TREE_TO_BOOL:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root)\n");
                sb.append("print(\"true\" if res else \"false\")\n");
                break;

            case TREE_TO_TREE:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root)\n");
                sb.append("print(format_tree(res))\n");
                break;

            case TREE_TO_INT_ARRAY:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case STRING_INT_TO_INT:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s, k)\n");
                sb.append("print(res)\n");
                break;

            case INT_TARGET_INT_ARRAY_TO_INT:
                sb.append("target = parse_target(raw_input)\n");
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(target, nums)\n");
                sb.append("print(res)\n");
                break;

            case TWO_STRINGS_TO_INT_ARRAY:
                sb.append("s = parse_string_param(raw_input, 's')\n");
                sb.append("p = parse_string_param(raw_input, 'p')\n");
                sb.append("res = sol.").append(method).append("(s, p)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_ARRAY_INT_TO_DOUBLE:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums, k)\n");
                sb.append("print(f\"{float(res):.5f}\")\n");
                break;

            case TWO_STRINGS_TO_STRING:
                sb.append("s = parse_string_param(raw_input, 's')\n");
                sb.append("t = parse_string_param(raw_input, 't')\n");
                sb.append("res = sol.").append(method).append("(s, t)\n");
                sb.append("print(res if res is not None else '')\n");
                break;

            case TWO_TREES_TO_BOOL:
                sb.append("p = parse_tree_param(raw_input, 1)\n");
                sb.append("q = parse_tree_param(raw_input, 2)\n");
                sb.append("res = sol.").append(method).append("(p, q)\n");
                sb.append("print(str(res).lower())\n");
                break;

            case TREE_INT_TO_BOOL:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("targetSum = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root, targetSum)\n");
                sb.append("print(str(res).lower())\n");
                break;

            case TREE_INT_TO_INT:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root, k)\n");
                sb.append("print(res)\n");
                break;

            case TREE_TO_INT_LIST_LIST:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("res = sol.").append(method).append("(root)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case TREE_TWO_NODES_TO_NODE:
                sb.append("root = parse_tree(raw_input)\n");
                sb.append("pVal = parse_int_param(raw_input, 'p')\n");
                sb.append("qVal = parse_int_param(raw_input, 'q')\n");
                sb.append("p = find_node(root, pVal)\n");
                sb.append("q = find_node(root, qVal)\n");
                sb.append("res = sol.").append(method).append("(root, p, q)\n");
                sb.append("print(res.val if res else 'null')\n");
                break;

            case MATRIX_INT_TO_MATRIX:
                sb.append("points = parse_matrix(raw_input)\n");
                sb.append("k = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(points, k)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case CHAR_ARRAY_INT_TO_INT:
                sb.append("tasks = parse_char_array(raw_input)\n");
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(tasks, n)\n");
                sb.append("print(res)\n");
                break;

            case STRING_TO_STRING:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s)\n");
                sb.append("print(res if res is not None else '')\n");
                break;

            case STRING_LIST_TO_BOOL:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("wordDict = parse_string_list(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s, wordDict)\n");
                sb.append("print(str(res).lower())\n");
                break;

            case CHAR_MATRIX_TO_INT:
                sb.append("grid = parse_char_matrix(raw_input)\n");
                sb.append("res = sol.").append(method).append("(grid)\n");
                sb.append("print(res)\n");
                break;

            case INT_MATRIX_TO_INT:
                sb.append("grid = parse_matrix(raw_input)\n");
                sb.append("res = sol.").append(method).append("(grid)\n");
                sb.append("print(res)\n");
                break;

            case MATRIX_INT_INT_INT_TO_MATRIX:
                sb.append("image = parse_matrix(raw_input)\n");
                sb.append("sr = parse_int_param(raw_input, 'sr')\n");
                sb.append("sc = parse_int_param(raw_input, 'sc')\n");
                sb.append("color = parse_int_param(raw_input, 'color')\n");
                sb.append("res = sol.").append(method).append("(image, sr, sc, color)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_MATRIX_TO_BOOL:
                sb.append("numCourses = parse_target(raw_input)\n");
                sb.append("prerequisites = parse_matrix(raw_input)\n");
                sb.append("res = sol.").append(method).append("(numCourses, prerequisites)\n");
                sb.append("print(str(res).lower())\n");
                break;

            case INT_MATRIX_TO_INT_ARRAY:
                sb.append("numCourses = parse_target(raw_input)\n");
                sb.append("prerequisites = parse_matrix(raw_input)\n");
                sb.append("res = sol.").append(method).append("(numCourses, prerequisites)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_MATRIX_TO_LIST_LIST:
                sb.append("heights = parse_matrix(raw_input)\n");
                sb.append("res = sol.").append(method).append("(heights)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case IN_PLACE_CHAR_MATRIX:
                sb.append("board = parse_char_matrix(raw_input)\n");
                sb.append("sol.").append(method).append("(board)\n");
                sb.append("print(format_char_matrix(board))\n");
                break;

            case TWO_STRINGS_LIST_TO_INT:
                sb.append("beginWord = parse_string_param(raw_input, 'beginWord')\n");
                sb.append("endWord = parse_string_param(raw_input, 'endWord')\n");
                sb.append("wordList = parse_string_list(raw_input)\n");
                sb.append("res = sol.").append(method).append("(beginWord, endWord, wordList)\n");
                sb.append("print(res)\n");
                break;

            case INT_ARRAY_TO_LIST_LIST:
                sb.append("nums = parse_int_array(raw_input)\n");
                sb.append("res = sol.").append(method).append("(nums)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_ARRAY_TARGET_TO_LIST_LIST:
                sb.append("candidates = parse_int_array(raw_input)\n");
                sb.append("target = parse_target(raw_input)\n");
                sb.append("res = sol.").append(method).append("(candidates, target)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_TO_STRING_LIST:
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(n)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case CHAR_MATRIX_STRING_TO_BOOL:
                sb.append("board = parse_char_matrix(raw_input)\n");
                sb.append("word = parse_string_param(raw_input, 'word')\n");
                sb.append("res = sol.").append(method).append("(board, word)\n");
                sb.append("print(str(res).lower())\n");
                break;

            case STRING_TO_STRING_LIST:
                sb.append("digits = parse_string(raw_input)\n");
                sb.append("res = sol.").append(method).append("(digits)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case STRING_TO_LIST_LIST_STRING:
                sb.append("s = parse_string(raw_input)\n");
                sb.append("res = sol.").append(method).append("(s)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case INT_TO_LIST_LIST_STRING:
                sb.append("n = parse_int(raw_input)\n");
                sb.append("res = sol.").append(method).append("(n)\n");
                sb.append("print(json.dumps(res if res is not None else []))\n");
                break;

            case TWO_INT_ARRAYS_TO_DOUBLE:
                sb.append("nums1 = parse_int_array_param(raw_input, 1)\n");
                sb.append("nums2 = parse_int_array_param(raw_input, 2)\n");
                sb.append("res = sol.").append(method).append("(nums1, nums2)\n");
                sb.append("print(f\"{float(res):.5f}\")\n");
                break;
        }

        return sb.toString();
    }

    private static void appendPythonHelpers(StringBuilder sb) {
        sb.append("def parse_int_array(text):\n");
        sb.append("    m = re.search(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if not m: return []\n");
        sb.append("    inside = m.group(1).strip()\n");
        sb.append("    if not inside: return []\n");
        sb.append("    return [int(p) for p in re.split(r'[,\\s]+', inside) if p.strip()]\n\n");

        sb.append("def parse_target(text):\n");
        sb.append("    m = re.search(r'(?:target|k)\\s*=\\s*(-?\\d+)', text)\n");
        sb.append("    if m: return int(m.group(1))\n");
        sb.append("    idx = text.find(']')\n");
        sb.append("    if idx != -1:\n");
        sb.append("        m2 = re.search(r'(-?\\d+)', text[idx+1:])\n");
        sb.append("        if m2: return int(m2.group(1))\n");
        sb.append("    return 0\n\n");

        sb.append("def parse_int(text):\n");
        sb.append("    m = re.search(r'(?:n|x|val)\\s*=\\s*(-?\\d+)', text)\n");
        sb.append("    if m: return int(m.group(1))\n");
        sb.append("    m2 = re.search(r'(-?\\d+)', text)\n");
        sb.append("    return int(m2.group(1)) if m2 else 0\n\n");

        sb.append("def parse_int_param(text, name):\n");
        sb.append("    m = re.search(r'\\b' + name + r'\\s*=\\s*(-?\\d+)', text)\n");
        sb.append("    return int(m.group(1)) if m else 0\n\n");

        sb.append("def parse_char_array(text):\n");
        sb.append("    m = re.search(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if m:\n");
        sb.append("        inside = m.group(1)\n");
        sb.append("        tokens = re.findall(r'[\"\\\']([^\"\\\']+)[\"\\\']|([a-zA-Z0-9])', inside)\n");
        sb.append("        return [t[0] or t[1] for t in tokens if t[0] or t[1]]\n");
        sb.append("    m2 = re.search(r's\\s*=\\s*\"([^\"]*)\"|\"([^\"]*)\"', text)\n");
        sb.append("    if m2:\n");
        sb.append("        val = m2.group(1) or m2.group(2)\n");
        sb.append("        return list(val)\n");
        sb.append("    return list(text.strip())\n\n");

        sb.append("def parse_string(text):\n");
        sb.append("    m = re.search(r's\\s*=\\s*\"([^\"]*)\"|\"([^\"]*)\"', text)\n");
        sb.append("    if m: return m.group(1) or m.group(2)\n");
        sb.append("    return re.sub(r'^s\\s*=\\s*', '', text).strip()\n\n");

        sb.append("def parse_string_param(text, name):\n");
        sb.append("    m = re.search(r'\\b' + name + r'\\s*=\\s*\"([^\"]*)\"', text)\n");
        sb.append("    if m: return m.group(1)\n");
        sb.append("    matches = re.findall(r'\"([^\"]*)\"', text)\n");
        sb.append("    if name == 't' and len(matches) >= 2: return matches[1]\n");
        sb.append("    return matches[0] if matches else \"\"\n\n");

        sb.append("def parse_string_array(text):\n");
        sb.append("    m = re.search(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if not m: return []\n");
        sb.append("    return re.findall(r'[\"\\\']([^\"\\\']*)[\"\\\']', m.group(1))\n\n");

        sb.append("def parse_linked_list(text):\n");
        sb.append("    arr = parse_int_array(text)\n");
        sb.append("    dummy = ListNode(0)\n");
        sb.append("    curr = dummy\n");
        sb.append("    for v in arr:\n");
        sb.append("        curr.next = ListNode(v)\n");
        sb.append("        curr = curr.next\n");
        sb.append("    return dummy.next\n\n");

        sb.append("def parse_linked_list_param(text, index):\n");
        sb.append("    matches = list(re.finditer(r'\\[.*?\\]', text, re.DOTALL))\n");
        sb.append("    if len(matches) >= index:\n");
        sb.append("        return parse_linked_list(matches[index - 1].group(0))\n");
        sb.append("    return parse_linked_list(text)\n\n");

        sb.append("def parse_int_after_list(text):\n");
        sb.append("    m = re.search(r'(?:n|k|x|val|node|target)\\s*=\\s*(-?\\d+)', text)\n");
        sb.append("    if m: return int(m.group(1))\n");
        sb.append("    idx = text.rfind(']')\n");
        sb.append("    if idx != -1:\n");
        sb.append("        m2 = re.search(r'(-?\\d+)', text[idx+1:])\n");
        sb.append("        if m2: return int(m2.group(1))\n");
        sb.append("    return 0\n\n");

        sb.append("def parse_linked_list_array(text):\n");
        sb.append("    first_idx = text.find('[')\n");
        sb.append("    last_idx = text.rfind(']')\n");
        sb.append("    if first_idx == -1 or last_idx == -1 or first_idx >= last_idx:\n");
        sb.append("        return []\n");
        sb.append("    inside = text[first_idx + 1:last_idx].strip()\n");
        sb.append("    if not inside:\n");
        sb.append("        return []\n");
        sb.append("    matches = re.findall(r'\\[(.*?)\\]', inside)\n");
        sb.append("    return [parse_linked_list('[' + m + ']') for m in matches]\n\n");

        sb.append("def format_linked_list(head):\n");
        sb.append("    res = []\n");
        sb.append("    seen = set()\n");
        sb.append("    curr = head\n");
        sb.append("    while curr:\n");
        sb.append("        if id(curr) in seen:\n");
        sb.append("            break\n");
        sb.append("        seen.add(id(curr))\n");
        sb.append("        res.append(curr.val)\n");
        sb.append("        curr = curr.next\n");
        sb.append("    return json.dumps(res)\n\n");

        sb.append("def parse_tree(text):\n");
        sb.append("    m = re.search(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if not m: return None\n");
        sb.append("    inside = m.group(1).strip()\n");
        sb.append("    if not inside: return None\n");
        sb.append("    parts = [p.strip() for p in re.split(r'[,\\s]+', inside) if p.strip()]\n");
        sb.append("    if not parts or parts[0] == 'null': return None\n");
        sb.append("    root = TreeNode(int(parts[0]))\n");
        sb.append("    q = deque([root])\n");
        sb.append("    i = 1\n");
        sb.append("    while q and i < len(parts):\n");
        sb.append("        curr = q.popleft()\n");
        sb.append("        if i < len(parts) and parts[i] != 'null':\n");
        sb.append("            curr.left = TreeNode(int(parts[i]))\n");
        sb.append("            q.append(curr.left)\n");
        sb.append("        i += 1\n");
        sb.append("        if i < len(parts) and parts[i] != 'null':\n");
        sb.append("            curr.right = TreeNode(int(parts[i]))\n");
        sb.append("            q.append(curr.right)\n");
        sb.append("        i += 1\n");
        sb.append("    return root\n\n");

        sb.append("def format_tree(root):\n");
        sb.append("    if not root: return \"[]\"\n");
        sb.append("    res = []\n");
        sb.append("    q = deque([root])\n");
        sb.append("    while q:\n");
        sb.append("        curr = q.popleft()\n");
        sb.append("        if curr:\n");
        sb.append("            res.append(str(curr.val))\n");
        sb.append("            q.append(curr.left)\n");
        sb.append("            q.append(curr.right)\n");
        sb.append("        else:\n");
        sb.append("            res.append(\"null\")\n");
        sb.append("    while res and res[-1] == 'null':\n");
        sb.append("        res.pop()\n");
        sb.append("    return \"[\" + \", \".join(res) + \"]\"\n\n");
        sb.append("def parse_matrix(text):\n");
        sb.append("    m = re.search(r'\\[\\s*\\[.*?\\]\\s*\\]', text, re.DOTALL)\n");
        sb.append("    if m:\n");
        sb.append("        try:\n");
        sb.append("            return json.loads(m.group(0))\n");
        sb.append("        except Exception:\n");
        sb.append("            pass\n");
        sb.append("    return []\n\n");
        sb.append("def parse_tree_param(text, index):\n");
        sb.append("    depth = 0\n");
        sb.append("    start = -1\n");
        sb.append("    found = 0\n");
        sb.append("    for i, ch in enumerate(text):\n");
        sb.append("        if ch == '[':\n");
        sb.append("            if depth == 0:\n");
        sb.append("                found += 1\n");
        sb.append("                if found == index: start = i\n");
        sb.append("            depth += 1\n");
        sb.append("        elif ch == ']':\n");
        sb.append("            depth -= 1\n");
        sb.append("            if depth == 0 and found == index and start != -1:\n");
        sb.append("                return parse_tree(text[start:i+1])\n");
        sb.append("    return None\n\n");
        sb.append("def find_node(root, val):\n");
        sb.append("    if not root: return None\n");
        sb.append("    if root.val == val: return root\n");
        sb.append("    left = find_node(root.left, val)\n");
        sb.append("    if left: return left\n");
        sb.append("    return find_node(root.right, val)\n\n");
        sb.append("def parse_string_list(text):\n");
        sb.append("    m = re.search(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if not m: return []\n");
        sb.append("    inside = m.group(1)\n");
        sb.append("    return [m[0] or m[1] for m in re.findall(r'\"([^\"]*)\"|\\'([^\\']*)\\'' , inside)]\n\n");
        sb.append("def parse_char_matrix(text):\n");
        sb.append("    import json\n");
        sb.append("    m = re.search(r'\\[\\s*\\[.*?\\]\\s*\\]', text, re.DOTALL)\n");
        sb.append("    if m:\n");
        sb.append("        try:\n");
        sb.append("            return json.loads(m.group(0))\n");
        sb.append("        except Exception:\n");
        sb.append("            pass\n");
        sb.append("    return []\n\n");
        sb.append("def format_char_matrix(board):\n");
        sb.append("    return json.dumps(board)\n\n");
        sb.append("def parse_int_array_param(text, index):\n");
        sb.append("    matches = re.findall(r'\\[(.*?)\\]', text, re.DOTALL)\n");
        sb.append("    if len(matches) >= index:\n");
        sb.append("        return parse_int_array('[' + matches[index - 1] + ']')\n");
        sb.append("    return []\n\n");
    }

    // =========================================================================
    // C++ HARNESS GENERATOR
    // =========================================================================

    public static String getCppHarness(DsaProblemDef def) {
        String method = def.getMethodName();
        DsaArchetype archetype = def.getArchetype();

        StringBuilder sb = new StringBuilder();
        sb.append("#include <iostream>\n");
        sb.append("#include <vector>\n");
        sb.append("#include <string>\n");
        sb.append("#include <sstream>\n");
        sb.append("#include <algorithm>\n");
        sb.append("#include <regex>\n");
        sb.append("#include <queue>\n");
        sb.append("#include <unordered_set>\n");
        sb.append("#include \"solution.h\"\n");
        sb.append("using namespace std;\n\n");

        appendCppHelpers(sb);

        sb.append("int main() {\n");
        sb.append("    string input, line;\n");
        sb.append("    while (getline(cin, line)) {\n");
        sb.append("        input += line + \"\\n\";\n");
        sb.append("    }\n");
        sb.append("    Solution sol;\n\n");

        switch (archetype) {
            case INT_ARRAY_TARGET_TO_INT_ARRAY:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int target = parseTarget(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(nums, target);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case INT_ARRAY_TARGET_TO_INT:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int target = parseTarget(input);\n");
                sb.append("    int res = sol.").append(method).append("(nums, target);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_ARRAY_TO_INT:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int res = sol.").append(method).append("(nums);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_ARRAY_TO_BOOL:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    bool res = sol.").append(method).append("(nums);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case INT_ARRAY_TO_INT_ARRAY:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(nums);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case IN_PLACE_INT_ARRAY:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                if (def.isReturnsLength()) {
                    sb.append("    int k = sol.").append(method).append("(nums);\n");
                    sb.append("    if (k < (int)nums.size()) nums.resize(k);\n");
                    sb.append("    cout << formatIntVector(nums) << endl;\n");
                } else {
                    sb.append("    sol.").append(method).append("(nums);\n");
                    sb.append("    cout << formatIntVector(nums) << endl;\n");
                }
                break;

            case INT_ARRAY_INT_TO_INT:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    int res = sol.").append(method).append("(nums, k);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_ARRAY_INT_TO_INT_ARRAY:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(nums, k);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case STRING_TO_BOOL:
                sb.append("    string s = parseString(input);\n");
                sb.append("    bool res = sol.").append(method).append("(s);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case STRING_TO_INT:
                sb.append("    string s = parseString(input);\n");
                sb.append("    int res = sol.").append(method).append("(s);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case TWO_STRINGS_TO_BOOL:
                sb.append("    string s = parseStringParam(input, \"s\");\n");
                sb.append("    string t = parseStringParam(input, \"t\");\n");
                sb.append("    bool res = sol.").append(method).append("(s, t);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case TWO_STRINGS_TO_INT:
                sb.append("    string s = parseStringParam(input, \"s\");\n");
                sb.append("    string t = parseStringParam(input, \"t\");\n");
                sb.append("    int res = sol.").append(method).append("(s, t);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case STRING_ARRAY_TO_STRING:
                sb.append("    vector<string> strs = parseStringArray(input);\n");
                sb.append("    string res = sol.").append(method).append("(strs);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case IN_PLACE_CHAR_ARRAY:
                sb.append("    vector<char> s = parseCharArray(input);\n");
                sb.append("    sol.").append(method).append("(s);\n");
                sb.append("    cout << formatCharVector(s) << endl;\n");
                break;

            case INT_TO_INT:
                sb.append("    int n = parseInt(input);\n");
                sb.append("    int res = sol.").append(method).append("(n);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_TO_BOOL:
                sb.append("    int n = parseInt(input);\n");
                sb.append("    bool res = sol.").append(method).append("(n);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case INT_TO_INT_ARRAY:
                sb.append("    int n = parseInt(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(n);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case TWO_INTS_TO_INT:
                sb.append("    int m = parseIntParam(input, \"m\");\n");
                sb.append("    int n = parseIntParam(input, \"n\");\n");
                sb.append("    int res = sol.").append(method).append("(m, n);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case LINKED_LIST_TO_LINKED_LIST:
                sb.append("    ListNode* head = parseLinkedList(input);\n");
                sb.append("    ListNode* res = sol.").append(method).append("(head);\n");
                sb.append("    cout << formatLinkedList(res) << endl;\n");
                break;

            case TWO_LINKED_LISTS_TO_LINKED_LIST:
                sb.append("    ListNode* l1 = parseLinkedListParam(input, 1);\n");
                sb.append("    ListNode* l2 = parseLinkedListParam(input, 2);\n");
                sb.append("    ListNode* res = sol.").append(method).append("(l1, l2);\n");
                sb.append("    cout << formatLinkedList(res) << endl;\n");
                break;

            case LINKED_LIST_INT_TO_LINKED_LIST:
                sb.append("    ListNode* head = parseLinkedList(input);\n");
                sb.append("    int n = parseIntAfterList(input);\n");
                sb.append("    ListNode* res = sol.").append(method).append("(head, n);\n");
                sb.append("    cout << formatLinkedList(res) << endl;\n");
                break;

            case IN_PLACE_LINKED_LIST:
                sb.append("    ListNode* head = parseLinkedList(input);\n");
                sb.append("    sol.").append(method).append("(head);\n");
                sb.append("    cout << formatLinkedList(head) << endl;\n");
                break;

            case LINKED_LIST_DELETE_NODE:
                sb.append("    ListNode* head = parseLinkedList(input);\n");
                sb.append("    int targetVal = parseIntAfterList(input);\n");
                sb.append("    ListNode* curr = head;\n");
                sb.append("    while (curr && curr->val != targetVal) curr = curr->next;\n");
                sb.append("    if (curr) sol.").append(method).append("(curr);\n");
                sb.append("    cout << formatLinkedList(head) << endl;\n");
                break;

            case LINKED_LIST_ARRAY_TO_LINKED_LIST:
                sb.append("    vector<ListNode*> lists = parseLinkedListArray(input);\n");
                sb.append("    ListNode* res = sol.").append(method).append("(lists);\n");
                sb.append("    cout << formatLinkedList(res) << endl;\n");
                break;

            case TREE_TO_INT:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    int res = sol.").append(method).append("(root);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case TREE_TO_BOOL:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    bool res = sol.").append(method).append("(root);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case TREE_TO_TREE:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    TreeNode* res = sol.").append(method).append("(root);\n");
                sb.append("    cout << formatTree(res) << endl;\n");
                break;

            case TREE_TO_INT_ARRAY:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(root);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case STRING_INT_TO_INT:
                sb.append("    string s = parseString(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    int res = sol.").append(method).append("(s, k);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_TARGET_INT_ARRAY_TO_INT:
                sb.append("    int target = parseTarget(input);\n");
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int res = sol.").append(method).append("(target, nums);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case TWO_STRINGS_TO_INT_ARRAY:
                sb.append("    string s = parseStringParam(input, \"s\");\n");
                sb.append("    string p = parseStringParam(input, \"p\");\n");
                sb.append("    vector<int> res = sol.").append(method).append("(s, p);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case INT_ARRAY_INT_TO_DOUBLE:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    double res = sol.").append(method).append("(nums, k);\n");
                sb.append("    printf(\"%.5f\\n\", res);\n");
                break;

            case TWO_STRINGS_TO_STRING:
                sb.append("    string s = parseStringParam(input, \"s\");\n");
                sb.append("    string t = parseStringParam(input, \"t\");\n");
                sb.append("    string res = sol.").append(method).append("(s, t);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case TWO_TREES_TO_BOOL:
                sb.append("    TreeNode* p = parseTreeParam(input, 1);\n");
                sb.append("    TreeNode* q = parseTreeParam(input, 2);\n");
                sb.append("    bool res = sol.").append(method).append("(p, q);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case TREE_INT_TO_BOOL:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    int targetSum = parseTarget(input);\n");
                sb.append("    bool res = sol.").append(method).append("(root, targetSum);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case TREE_INT_TO_INT:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    int res = sol.").append(method).append("(root, k);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case TREE_TO_INT_LIST_LIST:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(root);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case TREE_TWO_NODES_TO_NODE:
                sb.append("    TreeNode* root = parseTree(input);\n");
                sb.append("    int pVal = parseIntParam(input, \"p\");\n");
                sb.append("    int qVal = parseIntParam(input, \"q\");\n");
                sb.append("    TreeNode* p = findNode(root, pVal);\n");
                sb.append("    TreeNode* q = findNode(root, qVal);\n");
                sb.append("    TreeNode* res = sol.").append(method).append("(root, p, q);\n");
                sb.append("    if (res) cout << res->val << endl; else cout << \"null\" << endl;\n");
                break;

            case MATRIX_INT_TO_MATRIX:
                sb.append("    vector<vector<int>> points = parseMatrix(input);\n");
                sb.append("    int k = parseTarget(input);\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(points, k);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case CHAR_ARRAY_INT_TO_INT:
                sb.append("    vector<char> tasks = parseCharArray(input);\n");
                sb.append("    int n = parseInt(input);\n");
                sb.append("    int res = sol.").append(method).append("(tasks, n);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case STRING_TO_STRING:
                sb.append("    string s = parseString(input);\n");
                sb.append("    string res = sol.").append(method).append("(s);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case STRING_LIST_TO_BOOL:
                sb.append("    string s = parseString(input);\n");
                sb.append("    vector<string> wordDict = parseStringList(input);\n");
                sb.append("    bool res = sol.").append(method).append("(s, wordDict);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case CHAR_MATRIX_TO_INT:
                sb.append("    vector<vector<char>> grid = parseCharMatrix(input);\n");
                sb.append("    int res = sol.").append(method).append("(grid);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_MATRIX_TO_INT:
                sb.append("    vector<vector<int>> grid = parseMatrix(input);\n");
                sb.append("    int res = sol.").append(method).append("(grid);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case MATRIX_INT_INT_INT_TO_MATRIX:
                sb.append("    vector<vector<int>> image = parseMatrix(input);\n");
                sb.append("    int sr = parseIntParam(input, \"sr\");\n");
                sb.append("    int sc = parseIntParam(input, \"sc\");\n");
                sb.append("    int color = parseIntParam(input, \"color\");\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(image, sr, sc, color);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case INT_MATRIX_TO_BOOL:
                sb.append("    int numCourses = parseTarget(input);\n");
                sb.append("    vector<vector<int>> prerequisites = parseMatrix(input);\n");
                sb.append("    bool res = sol.").append(method).append("(numCourses, prerequisites);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case INT_MATRIX_TO_INT_ARRAY:
                sb.append("    int numCourses = parseTarget(input);\n");
                sb.append("    vector<vector<int>> prerequisites = parseMatrix(input);\n");
                sb.append("    vector<int> res = sol.").append(method).append("(numCourses, prerequisites);\n");
                sb.append("    cout << formatIntVector(res) << endl;\n");
                break;

            case INT_MATRIX_TO_LIST_LIST:
                sb.append("    vector<vector<int>> heights = parseMatrix(input);\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(heights);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case IN_PLACE_CHAR_MATRIX:
                sb.append("    vector<vector<char>> board = parseCharMatrix(input);\n");
                sb.append("    sol.").append(method).append("(board);\n");
                sb.append("    cout << formatCharMatrix(board) << endl;\n");
                break;

            case TWO_STRINGS_LIST_TO_INT:
                sb.append("    string beginWord = parseStringParam(input, \"beginWord\");\n");
                sb.append("    string endWord = parseStringParam(input, \"endWord\");\n");
                sb.append("    vector<string> wordList = parseStringList(input);\n");
                sb.append("    int res = sol.").append(method).append("(beginWord, endWord, wordList);\n");
                sb.append("    cout << res << endl;\n");
                break;

            case INT_ARRAY_TO_LIST_LIST:
                sb.append("    vector<int> nums = parseIntArray(input);\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(nums);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case INT_ARRAY_TARGET_TO_LIST_LIST:
                sb.append("    vector<int> candidates = parseIntArray(input);\n");
                sb.append("    int target = parseTarget(input);\n");
                sb.append("    vector<vector<int>> res = sol.").append(method).append("(candidates, target);\n");
                sb.append("    cout << formatMatrix(res) << endl;\n");
                break;

            case INT_TO_STRING_LIST:
                sb.append("    int n = parseInt(input);\n");
                sb.append("    vector<string> res = sol.").append(method).append("(n);\n");
                sb.append("    cout << formatStringVector(res) << endl;\n");
                break;

            case CHAR_MATRIX_STRING_TO_BOOL:
                sb.append("    vector<vector<char>> board = parseCharMatrix(input);\n");
                sb.append("    string word = parseStringParam(input, \"word\");\n");
                sb.append("    bool res = sol.").append(method).append("(board, word);\n");
                sb.append("    cout << (res ? \"true\" : \"false\") << endl;\n");
                break;

            case STRING_TO_STRING_LIST:
                sb.append("    string digits = parseString(input);\n");
                sb.append("    vector<string> res = sol.").append(method).append("(digits);\n");
                sb.append("    cout << formatStringVector(res) << endl;\n");
                break;

            case STRING_TO_LIST_LIST_STRING:
                sb.append("    string s = parseString(input);\n");
                sb.append("    vector<vector<string>> res = sol.").append(method).append("(s);\n");
                sb.append("    cout << formatStringMatrix(res) << endl;\n");
                break;

            case INT_TO_LIST_LIST_STRING:
                sb.append("    int n = parseInt(input);\n");
                sb.append("    vector<vector<string>> res = sol.").append(method).append("(n);\n");
                sb.append("    cout << formatStringMatrix(res) << endl;\n");
                break;

            case TWO_INT_ARRAYS_TO_DOUBLE:
                sb.append("    vector<int> nums1 = parseIntArrayParam(input, 1);\n");
                sb.append("    vector<int> nums2 = parseIntArrayParam(input, 2);\n");
                sb.append("    double res = sol.").append(method).append("(nums1, nums2);\n");
                sb.append("    printf(\"%.5f\\n\", res);\n");
                break;
        }

        sb.append("    return 0;\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static void appendCppHelpers(StringBuilder sb) {
        sb.append("static vector<int> parseIntArray(const string& text) {\n");
        sb.append("    vector<int> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    size_t end = text.find(']', start);\n");
        sb.append("    if (start == string::npos || end == string::npos) return res;\n");
        sb.append("    string inside = text.substr(start + 1, end - start - 1);\n");
        sb.append("    stringstream ss(inside);\n");
        sb.append("    string token;\n");
        sb.append("    while (getline(ss, token, ',')) {\n");
        sb.append("        stringstream ts(token);\n");
        sb.append("        int val;\n");
        sb.append("        if (ts >> val) res.push_back(val);\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");

        sb.append("static int parseTarget(const string& text) {\n");
        sb.append("    regex re(\"(?:target|k)\\\\s*=\\\\s*(-?\\\\d+)\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return stoi(m[1].str());\n");
        sb.append("    size_t end = text.find(']');\n");
        sb.append("    if (end != string::npos) {\n");
        sb.append("        string after = text.substr(end + 1);\n");
        sb.append("        regex re2(\"(-?\\\\d+)\");\n");
        sb.append("        smatch m2;\n");
        sb.append("        if (regex_search(after, m2, re2)) return stoi(m2[1].str());\n");
        sb.append("    }\n");
        sb.append("    return 0;\n");
        sb.append("}\n\n");

        sb.append("static int parseInt(const string& text) {\n");
        sb.append("    regex re(\"(?:n|x|val)\\\\s*=\\\\s*(-?\\\\d+)\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return stoi(m[1].str());\n");
        sb.append("    regex re2(\"(-?\\\\d+)\");\n");
        sb.append("    smatch m2;\n");
        sb.append("    if (regex_search(text, m2, re2)) return stoi(m2[1].str());\n");
        sb.append("    return 0;\n");
        sb.append("}\n\n");

        sb.append("static int parseIntParam(const string& text, const string& param) {\n");
        sb.append("    regex re(\"\\\\b\" + param + \"\\\\s*=\\\\s*(-?\\\\d+)\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return stoi(m[1].str());\n");
        sb.append("    return 0;\n");
        sb.append("}\n\n");

        sb.append("static vector<char> parseCharArray(const string& text) {\n");
        sb.append("    vector<char> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    size_t end = text.rfind(']');\n");
        sb.append("    if (start != string::npos && end != string::npos) {\n");
        sb.append("        string inside = text.substr(start + 1, end - start - 1);\n");
        sb.append("        regex re(\"[\\\"']([^\\\"']+)[\\\"']|([a-zA-Z0-9])\");\n");
        sb.append("        sregex_iterator next(inside.begin(), inside.end(), re);\n");
        sb.append("        sregex_iterator end_it;\n");
        sb.append("        while (next != end_it) {\n");
        sb.append("            smatch m = *next;\n");
        sb.append("            string val = m[1].matched ? m[1].str() : m[2].str();\n");
        sb.append("            if (!val.empty()) res.push_back(val[0]);\n");
        sb.append("            next++;\n");
        sb.append("        }\n");
        sb.append("        return res;\n");
        sb.append("    }\n");
        sb.append("    for (char c : text) if (!isspace(c)) res.push_back(c);\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");

        sb.append("static string parseString(const string& text) {\n");
        sb.append("    regex re(\"s\\\\s*=\\\\s*\\\"([^\\\"]*)\\\"|\\\"([^\\\"]*)\\\"\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return m[1].matched ? m[1].str() : m[2].str();\n");
        sb.append("    string s = regex_replace(text, regex(\"^\\\\s*s\\\\s*=\\\\s*\"), \"\");\n");
        sb.append("    s.erase(0, s.find_first_not_of(\" \\t\\r\\n\"));\n");
        sb.append("    s.erase(s.find_last_not_of(\" \\t\\r\\n\") + 1);\n");
        sb.append("    return s;\n");
        sb.append("}\n\n");

        sb.append("static string parseStringParam(const string& text, const string& param) {\n");
        sb.append("    regex re(\"\\\\b\" + param + \"\\\\s*=\\\\s*\\\"([^\\\"]*)\\\"\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return m[1].str();\n");
        sb.append("    regex re2(\"\\\"([^\\\"]*)\\\"\");\n");
        sb.append("    sregex_iterator next(text.begin(), text.end(), re2);\n");
        sb.append("    sregex_iterator end_it;\n");
        sb.append("    if (param == \"t\" && next != end_it) {\n");
        sb.append("        next++;\n");
        sb.append("        if (next != end_it) return (*next)[1].str();\n");
        sb.append("    }\n");
        sb.append("    if (next != end_it) return (*next)[1].str();\n");
        sb.append("    return \"\";\n");
        sb.append("}\n\n");

        sb.append("static vector<string> parseStringArray(const string& text) {\n");
        sb.append("    vector<string> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    size_t end = text.rfind(']');\n");
        sb.append("    if (start == string::npos || end == string::npos) return res;\n");
        sb.append("    string inside = text.substr(start + 1, end - start - 1);\n");
        sb.append("    regex re(\"[\\\"']([^\\\"']*)[\\\"']\");\n");
        sb.append("    sregex_iterator next(inside.begin(), inside.end(), re);\n");
        sb.append("    sregex_iterator end_it;\n");
        sb.append("    while (next != end_it) {\n");
        sb.append("        res.push_back((*next)[1].str());\n");
        sb.append("        next++;\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");

        sb.append("static ListNode* parseLinkedList(const string& text) {\n");
        sb.append("    vector<int> arr = parseIntArray(text);\n");
        sb.append("    ListNode dummy(0);\n");
        sb.append("    ListNode* curr = &dummy;\n");
        sb.append("    for (int v : arr) {\n");
        sb.append("        curr->next = new ListNode(v);\n");
        sb.append("        curr = curr->next;\n");
        sb.append("    }\n");
        sb.append("    return dummy.next;\n");
        sb.append("}\n\n");

        sb.append("static ListNode* parseLinkedListParam(const string& text, int index) {\n");
        sb.append("    size_t pos = 0;\n");
        sb.append("    for (int i = 0; i < index; i++) {\n");
        sb.append("        pos = text.find('[', pos);\n");
        sb.append("        if (pos == string::npos) break;\n");
        sb.append("        if (i < index - 1) pos++;\n");
        sb.append("    }\n");
        sb.append("    if (pos == string::npos) return parseLinkedList(text);\n");
        sb.append("    return parseLinkedList(text.substr(pos));\n");
        sb.append("}\n\n");

        sb.append("static TreeNode* parseTree(const string& text) {\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    size_t end = text.rfind(']');\n");
        sb.append("    if (start == string::npos || end == string::npos) return nullptr;\n");
        sb.append("    string inside = text.substr(start + 1, end - start - 1);\n");
        sb.append("    stringstream ss(inside);\n");
        sb.append("    string token;\n");
        sb.append("    vector<string> parts;\n");
        sb.append("    while (getline(ss, token, ',')) {\n");
        sb.append("        token.erase(0, token.find_first_not_of(\" \\t\\r\\n\"));\n");
        sb.append("        token.erase(token.find_last_not_of(\" \\t\\r\\n\") + 1);\n");
        sb.append("        if (!token.empty()) parts.push_back(token);\n");
        sb.append("    }\n");
        sb.append("    if (parts.empty() || parts[0] == \"null\") return nullptr;\n");
        sb.append("    TreeNode* root = new TreeNode(stoi(parts[0]));\n");
        sb.append("    queue<TreeNode*> q;\n");
        sb.append("    q.push(root);\n");
        sb.append("    size_t i = 1;\n");
        sb.append("    while (!q.empty() && i < parts.size()) {\n");
        sb.append("        TreeNode* curr = q.front(); q.pop();\n");
        sb.append("        if (i < parts.size() && parts[i] != \"null\") {\n");
        sb.append("            curr->left = new TreeNode(stoi(parts[i]));\n");
        sb.append("            q.push(curr->left);\n");
        sb.append("        }\n");
        sb.append("        i++;\n");
        sb.append("        if (i < parts.size() && parts[i] != \"null\") {\n");
        sb.append("            curr->right = new TreeNode(stoi(parts[i]));\n");
        sb.append("            q.push(curr->right);\n");
        sb.append("        }\n");
        sb.append("        i++;\n");
        sb.append("    }\n");
        sb.append("    return root;\n");
        sb.append("}\n\n");

        sb.append("static string formatIntVector(const vector<int>& arr) {\n");
        sb.append("    string out = \"[\";\n");
        sb.append("    for (size_t i = 0; i < arr.size(); i++) {\n");
        sb.append("        out += to_string(arr[i]) + (i + 1 < arr.size() ? \", \" : \"\");\n");
        sb.append("    }\n");
        sb.append("    return out + \"]\";\n");
        sb.append("}\n\n");

        sb.append("static string formatCharVector(const vector<char>& arr) {\n");
        sb.append("    string out = \"[\";\n");
        sb.append("    for (size_t i = 0; i < arr.size(); i++) {\n");
        sb.append("        out += string(\"\\\"\") + arr[i] + \"\\\"\" + (i + 1 < arr.size() ? \", \" : \"\");\n");
        sb.append("    }\n");
        sb.append("    return out + \"]\";\n");
        sb.append("}\n\n");

        sb.append("static int parseIntAfterList(const string& text) {\n");
        sb.append("    regex re(R\"((?:n|k|x|val|node|target)\\s*=\\s*(-?\\d+))\");\n");
        sb.append("    smatch m;\n");
        sb.append("    if (regex_search(text, m, re)) return stoi(m[1].str());\n");
        sb.append("    size_t end = text.rfind(']');\n");
        sb.append("    if (end != string::npos) {\n");
        sb.append("        string after = text.substr(end + 1);\n");
        sb.append("        regex re2(\"(-?\\\\d+)\");\n");
        sb.append("        smatch m2;\n");
        sb.append("        if (regex_search(after, m2, re2)) return stoi(m2[1].str());\n");
        sb.append("    }\n");
        sb.append("    return 0;\n");
        sb.append("}\n\n");

        sb.append("static vector<ListNode*> parseLinkedListArray(const string& text) {\n");
        sb.append("    vector<ListNode*> res;\n");
        sb.append("    size_t firstBracket = text.find('[');\n");
        sb.append("    size_t lastBracket = text.rfind(']');\n");
        sb.append("    if (firstBracket == string::npos || lastBracket == string::npos || firstBracket >= lastBracket) return res;\n");
        sb.append("    string inside = text.substr(firstBracket + 1, lastBracket - firstBracket - 1);\n");
        sb.append("    size_t i = 0;\n");
        sb.append("    while (i < inside.length()) {\n");
        sb.append("        size_t start = inside.find('[', i);\n");
        sb.append("        if (start == string::npos) break;\n");
        sb.append("        size_t end = inside.find(']', start);\n");
        sb.append("        if (end == string::npos) break;\n");
        sb.append("        string listStr = inside.substr(start, end - start + 1);\n");
        sb.append("        res.push_back(parseLinkedList(listStr));\n");
        sb.append("        i = end + 1;\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");

        sb.append("static string formatLinkedList(ListNode* head) {\n");
        sb.append("    string out = \"[\";\n");
        sb.append("    unordered_set<ListNode*> seen;\n");
        sb.append("    ListNode* curr = head;\n");
        sb.append("    while (curr) {\n");
        sb.append("        if (!seen.insert(curr).second) break;\n");
        sb.append("        out += to_string(curr->val) + (curr->next ? \", \" : \"\");\n");
        sb.append("        curr = curr->next;\n");
        sb.append("    }\n");
        sb.append("    return out + \"]\";\n");
        sb.append("}\n\n");

        sb.append("static string formatTree(TreeNode* root) {\n");
        sb.append("    if (!root) return \"[]\";\n");
        sb.append("    vector<string> list;\n");
        sb.append("    queue<TreeNode*> q;\n");
        sb.append("    q.push(root);\n");
        sb.append("    while (!q.empty()) {\n");
        sb.append("        TreeNode* curr = q.front(); q.pop();\n");
        sb.append("        if (curr) {\n");
        sb.append("            list.push_back(to_string(curr->val));\n");
        sb.append("            q.push(curr->left);\n");
        sb.append("            q.push(curr->right);\n");
        sb.append("        } else {\n");
        sb.append("            list.push_back(\"null\");\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("    while (!list.empty() && list.back() == \"null\") list.pop_back();\n");
        sb.append("    string out = \"[\";\n");
        sb.append("    for (size_t i = 0; i < list.size(); i++) {\n");
        sb.append("        out += list[i] + (i + 1 < list.size() ? \", \" : \"\");\n");
        sb.append("    }\n");
        sb.append("    return out + \"]\";\n");
        sb.append("}\n\n");
        sb.append("static vector<vector<int>> parseMatrix(const string& text) {\n");
        sb.append("    vector<vector<int>> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    if (start == string::npos) return res;\n");
        sb.append("    size_t idx = start + 1;\n");
        sb.append("    while ((idx = text.find('[', idx)) != string::npos) {\n");
        sb.append("        size_t end = text.find(']', idx);\n");
        sb.append("        if (end == string::npos) break;\n");
        sb.append("        res.push_back(parseIntArray(text.substr(idx, end - idx + 1)));\n");
        sb.append("        idx = end + 1;\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");
        sb.append("static string formatMatrix(const vector<vector<int>>& m) {\n");
        sb.append("    ostringstream oss;\n");
        sb.append("    oss << \"[\";\n");
        sb.append("    for (size_t i = 0; i < m.size(); i++) {\n");
        sb.append("        oss << formatIntVector(m[i]);\n");
        sb.append("        if (i + 1 < m.size()) oss << \", \";\n");
        sb.append("    }\n");
        sb.append("    oss << \"]\";\n");
        sb.append("    return oss.str();\n");
        sb.append("}\n\n");
        sb.append("static TreeNode* parseTreeParam(const string& text, int index) {\n");
        sb.append("    int depth = 0, start = -1, found = 0;\n");
        sb.append("    for (int i = 0; i < (int)text.size(); i++) {\n");
        sb.append("        if (text[i] == '[') {\n");
        sb.append("            if (depth == 0) {\n");
        sb.append("                found++;\n");
        sb.append("                if (found == index) start = i;\n");
        sb.append("            }\n");
        sb.append("            depth++;\n");
        sb.append("        } else if (text[i] == ']') {\n");
        sb.append("            depth--;\n");
        sb.append("            if (depth == 0 && found == index && start != -1) {\n");
        sb.append("                return parseTree(text.substr(start, i - start + 1));\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("    return nullptr;\n");
        sb.append("}\n\n");
        sb.append("static TreeNode* findNode(TreeNode* root, int val) {\n");
        sb.append("    if (!root) return nullptr;\n");
        sb.append("    if (root->val == val) return root;\n");
        sb.append("    TreeNode* left = findNode(root->left, val);\n");
        sb.append("    if (left) return left;\n");
        sb.append("    return findNode(root->right, val);\n");
        sb.append("}\n\n");
        sb.append("static vector<string> parseStringList(const string& text) {\n");
        sb.append("    vector<string> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    size_t end = text.rfind(']');\n");
        sb.append("    if (start == string::npos || end == string::npos) return res;\n");
        sb.append("    string inside = text.substr(start, end - start + 1);\n");
        sb.append("    regex re(\"\\\\\\\"([^\\\\\\\"]*)\\\\\\\"|'([^']*)'\");\n");
        sb.append("    auto words_begin = sregex_iterator(inside.begin(), inside.end(), re);\n");
        sb.append("    auto words_end = sregex_iterator();\n");
        sb.append("    for (sregex_iterator i = words_begin; i != words_end; ++i) {\n");
        sb.append("        smatch match = *i;\n");
        sb.append("        res.push_back(match[1].matched ? match[1].str() : match[2].str());\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");
        sb.append("static vector<vector<char>> parseCharMatrix(const string& text) {\n");
        sb.append("    vector<vector<char>> res;\n");
        sb.append("    size_t start = text.find('[');\n");
        sb.append("    if (start == string::npos) return res;\n");
        sb.append("    size_t idx = start + 1;\n");
        sb.append("    while ((idx = text.find('[', idx)) != string::npos) {\n");
        sb.append("        size_t end = text.find(']', idx);\n");
        sb.append("        if (end == string::npos) break;\n");
        sb.append("        res.push_back(parseCharArray(text.substr(idx, end - idx + 1)));\n");
        sb.append("        idx = end + 1;\n");
        sb.append("    }\n");
        sb.append("    return res;\n");
        sb.append("}\n\n");
        sb.append("static string formatCharMatrix(const vector<vector<char>>& m) {\n");
        sb.append("    ostringstream oss;\n");
        sb.append("    oss << \"[\";\n");
        sb.append("    for (size_t i = 0; i < m.size(); i++) {\n");
        sb.append("        oss << \"[\";\n");
        sb.append("        for (size_t j = 0; j < m[i].size(); j++) {\n");
        sb.append("            oss << \"\\\\\\\"\" << m[i][j] << \"\\\\\\\"\";\n");
        sb.append("            if (j + 1 < m[i].size()) oss << \", \";\n");
        sb.append("        }\n");
        sb.append("        oss << \"]\";\n");
        sb.append("        if (i + 1 < m.size()) oss << \", \";\n");
        sb.append("    }\n");
        sb.append("    oss << \"]\";\n");
        sb.append("    return oss.str();\n");
        sb.append("}\n\n");
        sb.append("static vector<int> parseIntArrayParam(const string& text, int index) {\n");
        sb.append("    int count = 0;\n");
        sb.append("    size_t idx = 0;\n");
        sb.append("    while ((idx = text.find('[', idx)) != string::npos) {\n");
        sb.append("        count++;\n");
        sb.append("        size_t end = text.find(']', idx);\n");
        sb.append("        if (end == string::npos) break;\n");
        sb.append("        if (count == index) {\n");
        sb.append("            return parseIntArray(text.substr(idx, end - idx + 1));\n");
        sb.append("        }\n");
        sb.append("        idx = end + 1;\n");
        sb.append("    }\n");
        sb.append("    return {};\n");
        sb.append("}\n\n");
        sb.append("static string formatStringVector(const vector<string>& v) {\n");
        sb.append("    ostringstream oss;\n");
        sb.append("    oss << \"[\";\n");
        sb.append("    for (size_t i = 0; i < v.size(); i++) {\n");
        sb.append("        oss << \"\\\\\\\"\" << v[i] << \"\\\\\\\"\";\n");
        sb.append("        if (i + 1 < v.size()) oss << \", \";\n");
        sb.append("    }\n");
        sb.append("    oss << \"]\";\n");
        sb.append("    return oss.str();\n");
        sb.append("}\n\n");
        sb.append("static string formatStringMatrix(const vector<vector<string>>& m) {\n");
        sb.append("    ostringstream oss;\n");
        sb.append("    oss << \"[\";\n");
        sb.append("    for (size_t i = 0; i < m.size(); i++) {\n");
        sb.append("        oss << formatStringVector(m[i]);\n");
        sb.append("        if (i + 1 < m.size()) oss << \", \";\n");
        sb.append("    }\n");
        sb.append("    oss << \"]\";\n");
        sb.append("    return oss.str();\n");
        sb.append("}\n");
    }
}
