package pl.edu.mimuw.cloudatlas.interpreter.query;
import pl.edu.mimuw.cloudatlas.interpreter.query.Absyn.*;

public class PrettyPrinter
{
  //For certain applications increasing the initial size of the buffer may improve performance.
  private static final int INITIAL_BUFFER_SIZE = 128;
  //You may wish to change the parentheses used in precedence.
  private static final String _L_PAREN = new String("(");
  private static final String _R_PAREN = new String(")");
  //You may wish to change render
  private static void render(String s)
  {
    if (s.equals("{"))
    {
       buf_.append("\n");
       indent();
       buf_.append(s);
       _n_ = _n_ + 2;
       buf_.append("\n");
       indent();
    }
    else if (s.equals("(") || s.equals("["))
       buf_.append(s);
    else if (s.equals(")") || s.equals("]"))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals("}"))
    {
       _n_ = _n_ - 2;
       backup();
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals(","))
    {
       backup();
       buf_.append(s);
       buf_.append(" ");
    }
    else if (s.equals(";"))
    {
       backup();
       buf_.append(s);
       buf_.append("\n");
       indent();
    }
    else if (s.equals("")) return;
    else
    {
       buf_.append(s);
       buf_.append(" ");
    }
  }


  //  print and show methods are defined for each category.
  public static String print(ListStatement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ListStatement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ListOrderItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ListOrderItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ListSelItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ListSelItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(ListCondExpr foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(ListCondExpr foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(Program foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(Program foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(Statement foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(Statement foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(Where foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(Where foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(OrderBy foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(OrderBy foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(OrderItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(OrderItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(Order foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(Order foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(Nulls foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(Nulls foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(SelItem foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(SelItem foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(BoolExpr foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(BoolExpr foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(CondExpr foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(CondExpr foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(BasicExpr foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(BasicExpr foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String print(RelOp foo)
  {
    pp(foo, 0);
    trim();
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  public static String show(RelOp foo)
  {
    sh(foo);
    String temp = buf_.toString();
    buf_.delete(0,buf_.length());
    return temp;
  }
  /***   You shouldn't need to change anything beyond this point.   ***/

  private static void pp(ListStatement foo, int _i_)
  {
     for (java.util.Iterator<Statement> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(";");
       } else {
         render("");
       }
     }
  }

  private static void pp(ListOrderItem foo, int _i_)
  {
     for (java.util.Iterator<OrderItem> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(ListSelItem foo, int _i_)
  {
     for (java.util.Iterator<SelItem> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(ListCondExpr foo, int _i_)
  {
     for (java.util.Iterator<CondExpr> it = foo.iterator(); it.hasNext();)
     {
       pp(it.next(), 0);
       if (it.hasNext()) {
         render(",");
       } else {
         render("");
       }
     }
  }

  private static void pp(Program foo, int _i_)
  {
    if (foo instanceof ProgramC)
    {
       ProgramC _programc = (ProgramC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_programc.liststatement_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(Statement foo, int _i_)
  {
    if (foo instanceof StatementC)
    {
       StatementC _statementc = (StatementC) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("SELECT");
       pp(_statementc.listselitem_, 0);
       pp(_statementc.where_, 0);
       pp(_statementc.orderby_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(Where foo, int _i_)
  {
    if (foo instanceof NoWhereC)
    {
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof WhereC)
    {
       WhereC _wherec = (WhereC) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("WHERE");
       pp(_wherec.condexpr_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(OrderBy foo, int _i_)
  {
    if (foo instanceof NoOrderByC)
    {
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof OrderByC)
    {
       OrderByC _orderbyc = (OrderByC) foo;
       if (_i_ > 0) render(_L_PAREN);
       render("ORDER");
       render("BY");
       pp(_orderbyc.listorderitem_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(OrderItem foo, int _i_)
  {
    if (foo instanceof OrderItemC)
    {
       OrderItemC _orderitemc = (OrderItemC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_orderitemc.condexpr_, 0);
       pp(_orderitemc.order_, 0);
       pp(_orderitemc.nulls_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(Order foo, int _i_)
  {
    if (foo instanceof AscOrderC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("ASC");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof DescOrderC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("DESC");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof NoOrderC)
    {
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(Nulls foo, int _i_)
  {
    if (foo instanceof NoNullsC)
    {
       if (_i_ > 0) render(_L_PAREN);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof NullFirstsC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("NULLS");
       render("FIRST");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof NullsLastC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("NULLS");
       render("LAST");
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(SelItem foo, int _i_)
  {
    if (foo instanceof SelItemC)
    {
       SelItemC _selitemc = (SelItemC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_selitemc.condexpr_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof AliasedSelItemC)
    {
       AliasedSelItemC _aliasedselitemc = (AliasedSelItemC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_aliasedselitemc.condexpr_, 0);
       render("AS");
       pp(_aliasedselitemc.qident_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(BoolExpr foo, int _i_)
  {
    if (foo instanceof BoolExprCmpC)
    {
       BoolExprCmpC _boolexprcmpc = (BoolExprCmpC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_boolexprcmpc.basicexpr_1, 0);
       pp(_boolexprcmpc.relop_, 0);
       pp(_boolexprcmpc.basicexpr_2, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof BoolExprRegExpC)
    {
       BoolExprRegExpC _boolexprregexpc = (BoolExprRegExpC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_boolexprregexpc.basicexpr_, 0);
       render("REGEXP");
       printQuoted(_boolexprregexpc.string_);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof BoolExprBasicExprC)
    {
       BoolExprBasicExprC _boolexprbasicexprc = (BoolExprBasicExprC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_boolexprbasicexprc.basicexpr_, 0);
       if (_i_ > 0) render(_R_PAREN);
    }
  }

  private static void pp(CondExpr foo, int _i_)
  {
    if (foo instanceof CondExprOrC)
    {
       CondExprOrC _condexprorc = (CondExprOrC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_condexprorc.condexpr_1, 0);
       render("OR");
       pp(_condexprorc.condexpr_2, 1);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof CondExprAndC)
    {
       CondExprAndC _condexprandc = (CondExprAndC) foo;
       if (_i_ > 1) render(_L_PAREN);
       pp(_condexprandc.condexpr_1, 1);
       render("AND");
       pp(_condexprandc.condexpr_2, 2);
       if (_i_ > 1) render(_R_PAREN);
    }
    else     if (foo instanceof CondExprNotC)
    {
       CondExprNotC _condexprnotc = (CondExprNotC) foo;
       if (_i_ > 2) render(_L_PAREN);
       render("NOT");
       pp(_condexprnotc.condexpr_, 2);
       if (_i_ > 2) render(_R_PAREN);
    }
    else     if (foo instanceof CondExprBoolExprC)
    {
       CondExprBoolExprC _condexprboolexprc = (CondExprBoolExprC) foo;
       if (_i_ > 2) render(_L_PAREN);
       pp(_condexprboolexprc.boolexpr_, 0);
       if (_i_ > 2) render(_R_PAREN);
    }
  }

  private static void pp(BasicExpr foo, int _i_)
  {
    if (foo instanceof BasicExprAddC)
    {
       BasicExprAddC _basicexpraddc = (BasicExprAddC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_basicexpraddc.basicexpr_1, 0);
       render("+");
       pp(_basicexpraddc.basicexpr_2, 1);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof BasicExprSubC)
    {
       BasicExprSubC _basicexprsubc = (BasicExprSubC) foo;
       if (_i_ > 0) render(_L_PAREN);
       pp(_basicexprsubc.basicexpr_1, 0);
       render("-");
       pp(_basicexprsubc.basicexpr_2, 1);
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof BasicExprMulC)
    {
       BasicExprMulC _basicexprmulc = (BasicExprMulC) foo;
       if (_i_ > 1) render(_L_PAREN);
       pp(_basicexprmulc.basicexpr_1, 1);
       render("*");
       pp(_basicexprmulc.basicexpr_2, 2);
       if (_i_ > 1) render(_R_PAREN);
    }
    else     if (foo instanceof BasicExprDivC)
    {
       BasicExprDivC _basicexprdivc = (BasicExprDivC) foo;
       if (_i_ > 1) render(_L_PAREN);
       pp(_basicexprdivc.basicexpr_1, 1);
       render("/");
       pp(_basicexprdivc.basicexpr_2, 2);
       if (_i_ > 1) render(_R_PAREN);
    }
    else     if (foo instanceof BasicExprModC)
    {
       BasicExprModC _basicexprmodc = (BasicExprModC) foo;
       if (_i_ > 1) render(_L_PAREN);
       pp(_basicexprmodc.basicexpr_1, 1);
       render("%");
       pp(_basicexprmodc.basicexpr_2, 2);
       if (_i_ > 1) render(_R_PAREN);
    }
    else     if (foo instanceof BasicExprNegC)
    {
       BasicExprNegC _basicexprnegc = (BasicExprNegC) foo;
       if (_i_ > 2) render(_L_PAREN);
       render("-");
       pp(_basicexprnegc.basicexpr_, 2);
       if (_i_ > 2) render(_R_PAREN);
    }
    else     if (foo instanceof EBoolC)
    {
       EBoolC _eboolc = (EBoolC) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_eboolc.qbool_, 0);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EIdentC)
    {
       EIdentC _eidentc = (EIdentC) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_eidentc.qident_, 0);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EFunC)
    {
       EFunC _efunc = (EFunC) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_efunc.qident_, 0);
       render("(");
       pp(_efunc.listcondexpr_, 0);
       render(")");
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EStrC)
    {
       EStrC _estrc = (EStrC) foo;
       if (_i_ > 3) render(_L_PAREN);
       printQuoted(_estrc.string_);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EIntC)
    {
       EIntC _eintc = (EIntC) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_eintc.qinteger_, 0);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EDblC)
    {
       EDblC _edblc = (EDblC) foo;
       if (_i_ > 3) render(_L_PAREN);
       pp(_edblc.qdouble_, 0);
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof ECondExprC)
    {
       ECondExprC _econdexprc = (ECondExprC) foo;
       if (_i_ > 3) render(_L_PAREN);
       render("(");
       pp(_econdexprc.condexpr_, 0);
       render(")");
       if (_i_ > 3) render(_R_PAREN);
    }
    else     if (foo instanceof EStmtC)
    {
       EStmtC _estmtc = (EStmtC) foo;
       if (_i_ > 3) render(_L_PAREN);
       render("(");
       pp(_estmtc.statement_, 0);
       render(")");
       if (_i_ > 3) render(_R_PAREN);
    }
  }

  private static void pp(RelOp foo, int _i_)
  {
    if (foo instanceof RelOpGtC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render(">");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof RelOpEqC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("=");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof RelOpNeC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("<>");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof RelOpLtC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("<");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof RelOpLeC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render("<=");
       if (_i_ > 0) render(_R_PAREN);
    }
    else     if (foo instanceof RelOpGeC)
    {
       if (_i_ > 0) render(_L_PAREN);
       render(">=");
       if (_i_ > 0) render(_R_PAREN);
    }
  }


  private static void sh(ListStatement foo)
  {
     for (java.util.Iterator<Statement> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(ListOrderItem foo)
  {
     for (java.util.Iterator<OrderItem> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(ListSelItem foo)
  {
     for (java.util.Iterator<SelItem> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(ListCondExpr foo)
  {
     for (java.util.Iterator<CondExpr> it = foo.iterator(); it.hasNext();)
     {
       sh(it.next());
       if (it.hasNext())
         render(",");
     }
  }

  private static void sh(Program foo)
  {
    if (foo instanceof ProgramC)
    {
       ProgramC _programc = (ProgramC) foo;
       render("(");
       render("ProgramC");
       render("[");
       sh(_programc.liststatement_);
       render("]");
       render(")");
    }
  }

  private static void sh(Statement foo)
  {
    if (foo instanceof StatementC)
    {
       StatementC _statementc = (StatementC) foo;
       render("(");
       render("StatementC");
       render("[");
       sh(_statementc.listselitem_);
       render("]");
       sh(_statementc.where_);
       sh(_statementc.orderby_);
       render(")");
    }
  }

  private static void sh(Where foo)
  {
    if (foo instanceof NoWhereC)
    {
       render("NoWhereC");
    }
    if (foo instanceof WhereC)
    {
       WhereC _wherec = (WhereC) foo;
       render("(");
       render("WhereC");
       sh(_wherec.condexpr_);
       render(")");
    }
  }

  private static void sh(OrderBy foo)
  {
    if (foo instanceof NoOrderByC)
    {
       render("NoOrderByC");
    }
    if (foo instanceof OrderByC)
    {
       OrderByC _orderbyc = (OrderByC) foo;
       render("(");
       render("OrderByC");
       render("[");
       sh(_orderbyc.listorderitem_);
       render("]");
       render(")");
    }
  }

  private static void sh(OrderItem foo)
  {
    if (foo instanceof OrderItemC)
    {
       OrderItemC _orderitemc = (OrderItemC) foo;
       render("(");
       render("OrderItemC");
       sh(_orderitemc.condexpr_);
       sh(_orderitemc.order_);
       sh(_orderitemc.nulls_);
       render(")");
    }
  }

  private static void sh(Order foo)
  {
    if (foo instanceof AscOrderC)
    {
       render("AscOrderC");
    }
    if (foo instanceof DescOrderC)
    {
       render("DescOrderC");
    }
    if (foo instanceof NoOrderC)
    {
       render("NoOrderC");
    }
  }

  private static void sh(Nulls foo)
  {
    if (foo instanceof NoNullsC)
    {
       render("NoNullsC");
    }
    if (foo instanceof NullFirstsC)
    {
       render("NullFirstsC");
    }
    if (foo instanceof NullsLastC)
    {
       render("NullsLastC");
    }
  }

  private static void sh(SelItem foo)
  {
    if (foo instanceof SelItemC)
    {
       SelItemC _selitemc = (SelItemC) foo;
       render("(");
       render("SelItemC");
       sh(_selitemc.condexpr_);
       render(")");
    }
    if (foo instanceof AliasedSelItemC)
    {
       AliasedSelItemC _aliasedselitemc = (AliasedSelItemC) foo;
       render("(");
       render("AliasedSelItemC");
       sh(_aliasedselitemc.condexpr_);
       sh(_aliasedselitemc.qident_);
       render(")");
    }
  }

  private static void sh(BoolExpr foo)
  {
    if (foo instanceof BoolExprCmpC)
    {
       BoolExprCmpC _boolexprcmpc = (BoolExprCmpC) foo;
       render("(");
       render("BoolExprCmpC");
       sh(_boolexprcmpc.basicexpr_1);
       sh(_boolexprcmpc.relop_);
       sh(_boolexprcmpc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BoolExprRegExpC)
    {
       BoolExprRegExpC _boolexprregexpc = (BoolExprRegExpC) foo;
       render("(");
       render("BoolExprRegExpC");
       sh(_boolexprregexpc.basicexpr_);
       sh(_boolexprregexpc.string_);
       render(")");
    }
    if (foo instanceof BoolExprBasicExprC)
    {
       BoolExprBasicExprC _boolexprbasicexprc = (BoolExprBasicExprC) foo;
       render("(");
       render("BoolExprBasicExprC");
       sh(_boolexprbasicexprc.basicexpr_);
       render(")");
    }
  }

  private static void sh(CondExpr foo)
  {
    if (foo instanceof CondExprOrC)
    {
       CondExprOrC _condexprorc = (CondExprOrC) foo;
       render("(");
       render("CondExprOrC");
       sh(_condexprorc.condexpr_1);
       sh(_condexprorc.condexpr_2);
       render(")");
    }
    if (foo instanceof CondExprAndC)
    {
       CondExprAndC _condexprandc = (CondExprAndC) foo;
       render("(");
       render("CondExprAndC");
       sh(_condexprandc.condexpr_1);
       sh(_condexprandc.condexpr_2);
       render(")");
    }
    if (foo instanceof CondExprNotC)
    {
       CondExprNotC _condexprnotc = (CondExprNotC) foo;
       render("(");
       render("CondExprNotC");
       sh(_condexprnotc.condexpr_);
       render(")");
    }
    if (foo instanceof CondExprBoolExprC)
    {
       CondExprBoolExprC _condexprboolexprc = (CondExprBoolExprC) foo;
       render("(");
       render("CondExprBoolExprC");
       sh(_condexprboolexprc.boolexpr_);
       render(")");
    }
  }

  private static void sh(BasicExpr foo)
  {
    if (foo instanceof BasicExprAddC)
    {
       BasicExprAddC _basicexpraddc = (BasicExprAddC) foo;
       render("(");
       render("BasicExprAddC");
       sh(_basicexpraddc.basicexpr_1);
       sh(_basicexpraddc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BasicExprSubC)
    {
       BasicExprSubC _basicexprsubc = (BasicExprSubC) foo;
       render("(");
       render("BasicExprSubC");
       sh(_basicexprsubc.basicexpr_1);
       sh(_basicexprsubc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BasicExprMulC)
    {
       BasicExprMulC _basicexprmulc = (BasicExprMulC) foo;
       render("(");
       render("BasicExprMulC");
       sh(_basicexprmulc.basicexpr_1);
       sh(_basicexprmulc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BasicExprDivC)
    {
       BasicExprDivC _basicexprdivc = (BasicExprDivC) foo;
       render("(");
       render("BasicExprDivC");
       sh(_basicexprdivc.basicexpr_1);
       sh(_basicexprdivc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BasicExprModC)
    {
       BasicExprModC _basicexprmodc = (BasicExprModC) foo;
       render("(");
       render("BasicExprModC");
       sh(_basicexprmodc.basicexpr_1);
       sh(_basicexprmodc.basicexpr_2);
       render(")");
    }
    if (foo instanceof BasicExprNegC)
    {
       BasicExprNegC _basicexprnegc = (BasicExprNegC) foo;
       render("(");
       render("BasicExprNegC");
       sh(_basicexprnegc.basicexpr_);
       render(")");
    }
    if (foo instanceof EBoolC)
    {
       EBoolC _eboolc = (EBoolC) foo;
       render("(");
       render("EBoolC");
       sh(_eboolc.qbool_);
       render(")");
    }
    if (foo instanceof EIdentC)
    {
       EIdentC _eidentc = (EIdentC) foo;
       render("(");
       render("EIdentC");
       sh(_eidentc.qident_);
       render(")");
    }
    if (foo instanceof EFunC)
    {
       EFunC _efunc = (EFunC) foo;
       render("(");
       render("EFunC");
       sh(_efunc.qident_);
       render("[");
       sh(_efunc.listcondexpr_);
       render("]");
       render(")");
    }
    if (foo instanceof EStrC)
    {
       EStrC _estrc = (EStrC) foo;
       render("(");
       render("EStrC");
       sh(_estrc.string_);
       render(")");
    }
    if (foo instanceof EIntC)
    {
       EIntC _eintc = (EIntC) foo;
       render("(");
       render("EIntC");
       sh(_eintc.qinteger_);
       render(")");
    }
    if (foo instanceof EDblC)
    {
       EDblC _edblc = (EDblC) foo;
       render("(");
       render("EDblC");
       sh(_edblc.qdouble_);
       render(")");
    }
    if (foo instanceof ECondExprC)
    {
       ECondExprC _econdexprc = (ECondExprC) foo;
       render("(");
       render("ECondExprC");
       sh(_econdexprc.condexpr_);
       render(")");
    }
    if (foo instanceof EStmtC)
    {
       EStmtC _estmtc = (EStmtC) foo;
       render("(");
       render("EStmtC");
       sh(_estmtc.statement_);
       render(")");
    }
  }

  private static void sh(RelOp foo)
  {
    if (foo instanceof RelOpGtC)
    {
       render("RelOpGtC");
    }
    if (foo instanceof RelOpEqC)
    {
       render("RelOpEqC");
    }
    if (foo instanceof RelOpNeC)
    {
       render("RelOpNeC");
    }
    if (foo instanceof RelOpLtC)
    {
       render("RelOpLtC");
    }
    if (foo instanceof RelOpLeC)
    {
       render("RelOpLeC");
    }
    if (foo instanceof RelOpGeC)
    {
       render("RelOpGeC");
    }
  }


  private static void pp(String s, int _i_) { buf_.append(s); buf_.append(" "); }
  private static void sh(String s) { printQuoted(s); }
  private static void printQuoted(String s) { render("\"" + s + "\""); }
  private static void indent()
  {
    int n = _n_;
    while (n > 0)
    {
      buf_.append(" ");
      n--;
    }
  }
  private static void backup()
  {
     if (buf_.charAt(buf_.length() - 1) == ' ') {
      buf_.setLength(buf_.length() - 1);
    }
  }
  private static void trim()
  {
     while (buf_.length() > 0 && buf_.charAt(0) == ' ')
        buf_.deleteCharAt(0); 
    while (buf_.length() > 0 && buf_.charAt(buf_.length()-1) == ' ')
        buf_.deleteCharAt(buf_.length()-1);
  }
  private static int _n_ = 0;
  private static StringBuilder buf_ = new StringBuilder(INITIAL_BUFFER_SIZE);
}

