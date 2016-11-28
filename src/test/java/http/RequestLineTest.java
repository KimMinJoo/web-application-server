package http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RequestLineTest {
	@Test
	public void create_get_method() {
		//given
		String request = "GET /index.html HTTP/1.1";
		String expectedMethod = HttpMethod.GET.name();
		String expectedPath = "/index.html";
		
		//when
		RequestLine line = new RequestLine(request);

		//then
		assertEquals(expectedMethod, line.getMethod().name());
		assertEquals(expectedPath, line.getPath());
	}
	
	@Test
	public void create_post_method() {
		//given
		String request = "POST /index.html HTTP/1.1";
		String expectedMethod = HttpMethod.POST.name();
		String expectedPath = "/index.html";
		
		//when
		RequestLine line = new RequestLine(request);

		//then
		assertEquals(expectedMethod, line.getMethod().name());
		assertEquals(expectedPath, line.getPath());
	}
	
	@Test
	public void create_path_and_params() {
		//given
		String request = "GET /user/create?userId=java&password=spring HTTP/1.1";
		String expectedMethod = HttpMethod.GET.name();
		String expectedPath = "/user/create";
		int expectedParamSize = 2;		
		
		//when
		RequestLine line = new RequestLine(request);
		
		//then
		assertEquals(expectedMethod, line.getMethod().name());
		assertEquals(expectedPath, line.getPath());
		assertEquals(expectedParamSize, line.getParams().size());
	}

}
