package wheel.lang;

/** Can consume any value without ever throwing IllegalArgument. */
public interface Omnivore<VO> extends Consumer<VO> {

	@Override
	void consume(VO valueObject);
	
}
