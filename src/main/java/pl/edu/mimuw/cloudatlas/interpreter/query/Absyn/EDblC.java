package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class EDblC extends BasicExpr {
  public final String qdouble_;

  public EDblC(String p1) { qdouble_ = p1; }

  public <R,A> R accept(BasicExpr.Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof EDblC) {
      EDblC x = (EDblC)o;
      return this.qdouble_.equals(x.qdouble_);
    }
    return false;
  }

  public int hashCode() {
    return this.qdouble_.hashCode();
  }


}