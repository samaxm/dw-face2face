package online.decentworld.face2face.service.security.request;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 记录请求次数
 * @author Sammax
 *
 */
public class RequestRecorder {
	/**
	 * ip+method 请求ip和被请求方法名组成key
	 */
	private String key;
	/**
	 * 请求的时间线
	 */
	private ConcurrentLinkedQueue<Long> timeline;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ConcurrentLinkedQueue<Long> getTimeline() {
		return timeline;
	}
	public void setTimeline(ConcurrentLinkedQueue<Long> timeline) {
		this.timeline = timeline;
	}
	public RequestRecorder(String key, long time) {
		super();
		this.key = key;
		this.timeline = new ConcurrentLinkedQueue<Long>();
		timeline.add(time);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestRecorder other = (RequestRecorder) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
