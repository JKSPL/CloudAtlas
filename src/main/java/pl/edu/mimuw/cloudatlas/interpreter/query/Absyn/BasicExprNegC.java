package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class BasicExprNegC extends BasicExpr {
  public final BasicExpr basicexpr_;

  public BasicExprNegC(BasicExpr p1) { basicexpr_ = p1; }

  public <R,A> R accept(BasicExpr.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof BasicExprNegC) {
      BasicExprNegC x = (BasicExprNegC)o;
      return this.basicexpr_.equals(x.basicexpr_);
    }
    return false;
  }

  public int hashCode() {
    return this.basicexpr_.hashCode();
  }


}