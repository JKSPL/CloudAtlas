package pl.edu.mimuw.cloudatlas.interpreter.query.Absyn; // Java Package generated by the BNF Converter.

public abstract class OrderItem implements java.io.Serializable {
  public abstract <R,A> R accept(OrderItem.Visitor<R,A> v, A arg);
  public interface Visitor <R,A> {
    public R visit(OrderItemC p, A arg);

  }

}
