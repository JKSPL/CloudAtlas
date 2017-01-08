package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class CondExprNotC extends CondExpr {
  public final CondExpr condexpr_;

  public CondExprNotC(CondExpr p1) { condexpr_ = p1; }

  public <R,A> R accept(CondExpr.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof CondExprNotC) {
      CondExprNotC x = (CondExprNotC)o;
      return this.condexpr_.equals(x.condexpr_);
    }
    return false;
  }

  public int hashCode() {
    return this.condexpr_.hashCode();
  }


}