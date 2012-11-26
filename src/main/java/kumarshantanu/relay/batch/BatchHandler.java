package kumarshantanu.relay.batch;

public interface BatchHandler<InputType> {

	public void handle(Iterable<InputType> list) throws Exception;

}
