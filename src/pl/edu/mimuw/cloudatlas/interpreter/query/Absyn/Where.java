package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public abstract class Where implements java.io.Serializable {
  public abstract <R,A> R accept(Where.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(NoWhereC p, A arg);
    public R visit(WhereC p, A arg);

  }

}
