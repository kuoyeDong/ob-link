package com.onbright.oblink.smartconfig;

import android.content.Context;

import java.util.List;

public class EsptouchTask implements IEsptouchTask {

	public __EsptouchTask _mEsptouchTask;
	private IEsptouchTaskParameter _mParameter;

	/**
	 * Constructor of EsptouchTask
	 * 
	 * @param apSsid
	 *            the Ap's ssid
	 * @param apBssid
	 *            the Ap's bssid
	 * @param apPassword
	 *            the Ap's password
	 * @param context
	 *            the Context of the Application
	 */
	public EsptouchTask(String apSsid, String apBssid, String apPassword, Context context) {
		_mParameter = new EsptouchTaskParameter(context);
		_mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword,
				context, _mParameter, false);
	}
	
	/**
	 * @deprecated Use the new {{@link #EsptouchTask(String, String, String, Context)} API
	 */
	public EsptouchTask(String apSsid, String apBssid, String apPassword,
			boolean isSsidHidden, Context context) {
		this(apSsid, apBssid, apPassword, context);
	}
	
	/**
	 * Constructor of EsptouchTask
	 * 
	 * @param apSsid
	 *            the Ap's ssid
	 * @param apBssid
	 *            the Ap's bssid
	 * @param apPassword
	 *            the Ap's password
	 * @param timeoutMillisecond
	 *            (it should be >= 15000+6000) millisecond of total timeout
	 * @param context
	 *            the Context of the Application
	 */
	public EsptouchTask(String apSsid, String apBssid, String apPassword, int timeoutMillisecond, Context context) {
		_mParameter = new EsptouchTaskParameter(context);
		_mParameter.setWaitUdpTotalMillisecond(timeoutMillisecond);
		_mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword,
				context, _mParameter, false);
	}

	/**
	 * @deprecated Use the new {{@link #EsptouchTask(String, String, String, int, Context)} API
	 */
	public EsptouchTask(String apSsid, String apBssid, String apPassword,
			boolean isSsidHidden, int timeoutMillisecond, Context context) {
		this(apSsid, apBssid, apPassword, context);
	}

	@Override
	public void interrupt() {
		_mEsptouchTask.interrupt();
	}

	@Override
	public IEsptouchResult executeForResult() throws RuntimeException {
		return _mEsptouchTask.executeForResult();
	}

	@Override
	public boolean isCancelled() {
		return _mEsptouchTask.isCancelled();
	}

	@Override
	public List<IEsptouchResult> executeForResults(int expectTaskResultCount)
			throws RuntimeException {
		if (expectTaskResultCount <= 0) {
			expectTaskResultCount = Integer.MAX_VALUE;
		}
		return _mEsptouchTask.executeForResults(expectTaskResultCount);
	}

	@Override
	public void setEsptouchListener(IEsptouchListener esptouchListener) {
		_mEsptouchTask.setEsptouchListener(esptouchListener);
	}
}