package org.chuck.view.pullxview;

public interface Pullable
{
	/**
	 * �ж��Ƿ���������������Ҫ�������ܿ���ֱ��return false
	 * 
	 * @return true�������������򷵻�false
	 */
	boolean canPullDown();
	boolean canPullUp();
}
