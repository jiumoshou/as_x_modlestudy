package com.junit;

import junit.framework.TestCase;

/**
 * 设置环境:
 * <工程右键,properties> -- <Java Build Path; Libraries; Add Library> -- <Junit> -- <JUnit4> -- ok finish
 * 运行工程：
 * <工程右键.run as> -- <Junit Test; user configuration specific setting; Android Junit Test> -- ok finish
 *
 * @author YLine
 *         <p/>
 *         2016年6月22日 下午10:16:27
 */
public class AdderTest extends TestCase
{
	private AdderImpl adder;

	@Override
	protected void setUp() throws Exception
	{
		adder = new AdderImpl();
		super.setUp();
	}
	
	public void testAdd()
	{
		assertEquals(0, adder.add(0, 0));
	}

	@Override
	protected void tearDown() throws Exception
	{
		adder = null;
		super.tearDown();
	}
}
