package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public class NoWhereC extends Where {

  public NoWhereC() { }

  public <R,A> R accept(Visitor<R,A> v, A arg) { return v.visit(this, arg); }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof NoWhereC) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return 37;
  }


}
