package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /** 无符号整数 */
    Uint,
    /** 标识符 */
    Ident,
    /** Begin */
    Begin,
    /** End */
    End,
    /** Var */
    Var,
    /** Const */
    Const,
    /** Print */
    Print,
    /** 加号 */
    Plus,
    /** 减号 */
    Minus,
    /** 乘号 */
    Mult,
    /** 除号 */
    Div,
    /** 等号 */
    Equal,
    /** 分号 */
    Semicolon,
    /** 左括号 */
    LParen,
    /** 右括号 */
    RParen,
    /** 文件尾 */
    EOF,
    /* */
    Fn, //'fn'
    Let, //'let'
    As, //'as'
    While, //'while'
    If, //'if'
    Else, //'else'
    Return, //'return'
    Break, //'break'
    Continue, //'continue'
    Eq, //'=='
    Neq, //'!='
    Lt, //'<'
    Gt, //'>'
    Le, //'<='
    Ge, //'>='
    LBrace, //'{'
    RBrace, //'}'
    Arrow, //'->'
    Comma, //','
    Colon, //':'
    StringL, // ' "xxx" '
    Int,
    Void,

    Comment; //' //xx '



    @Override
    public String toString() {
        switch (this) {
            case None:
                return "NullToken";
            case Begin:
                return "Begin";
            case Const:
                return "Const";
            case Div:
                return "DivisionSign";
            case EOF:
                return "EOF";
            case End:
                return "End";
            case Equal:
                return "EqualSign";
            case Ident:
                return "Identifier";
            case LParen:
                return "LeftBracket";
            case Minus:
                return "MinusSign";
            case Mult:
                return "MultiplicationSign";
            case Plus:
                return "PlusSign";
            case Print:
                return "Print";
            case RParen:
                return "RightBracket";
            case Semicolon:
                return "Semicolon";
            case Uint:
                return "UnsignedInteger";
            case Var:
                return "Var";
            default:
                return "InvalidToken";
        }
    }
}
