class Token {
    private String type;
    private String value;
    private int line;
    private int column;

    public Token(String type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public String getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}

