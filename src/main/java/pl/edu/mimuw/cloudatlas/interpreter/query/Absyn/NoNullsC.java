package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class NoNullsC extends Nulls {

  public NoNullsC() { }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof NoNullsC) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
