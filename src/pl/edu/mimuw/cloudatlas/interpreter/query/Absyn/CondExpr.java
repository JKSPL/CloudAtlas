package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public abstract class CondExpr implements java.io.Serializable {
  public abstract <R,A> R accept(CondExpr.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(CondExprOrC p, A arg);
    public R visit(CondExprAndC p, A arg);
    public R visit(CondExprNotC p, A arg);
    public R visit(CondExprBoolExprC p, A arg);

  }

}
