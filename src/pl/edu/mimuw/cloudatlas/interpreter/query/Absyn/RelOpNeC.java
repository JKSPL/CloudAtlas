package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class RelOpNeC extends RelOp {

  public RelOpNeC() { }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof RelOpNeC) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
