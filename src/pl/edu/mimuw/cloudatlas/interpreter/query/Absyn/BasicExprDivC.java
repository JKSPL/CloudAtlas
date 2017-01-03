package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class BasicExprDivC extends BasicExpr {
  public final BasicExpr basicexpr_1, basicexpr_2;

  public BasicExprDivC(BasicExpr p1, BasicExpr p2) { basicexpr_1 = p1; basicexpr_2 = p2; }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof BasicExprDivC) {
      BasicExprDivC x = (BasicExprDivC)o;
      return this.basicexpr_1.equals(x.basicexpr_1) && this.basicexpr_2.equals(x.basicexpr_2);
    }
    return false;
  }

  public int hashCode() {
    return 37*(this.basicexpr_1.hashCode())+this.basicexpr_2.hashCode();
  }


}
