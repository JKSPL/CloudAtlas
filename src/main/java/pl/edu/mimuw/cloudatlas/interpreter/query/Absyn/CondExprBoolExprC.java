package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class CondExprBoolExprC extends CondExpr {
  public final BoolExpr boolexpr_;

  public CondExprBoolExprC(BoolExpr p1) { boolexpr_ = p1; }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof CondExprBoolExprC) {
      CondExprBoolExprC x = (CondExprBoolExprC)o;
      return this.boolexpr_.equals(x.boolexpr_);
    }
    return false;
  }

  public int hashCode() {
    return this.boolexpr_.hashCode();
  }


}
