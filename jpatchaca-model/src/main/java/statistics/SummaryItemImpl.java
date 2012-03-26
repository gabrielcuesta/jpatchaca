package statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;

public class SummaryItemImpl implements SummaryItem {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"E "
					+ FastDateFormat.getDateInstance(FastDateFormat.SHORT)
							.getPattern());
	private final Date date;
	private final String taskName;
	private final double hours;
	private final String formatedDate;

	public SummaryItemImpl(final Date date, final String taskName,
			final double hours) {
		this(date, taskName, SIMPLE_DATE_FORMAT.format(date), hours);
	}

	public SummaryItemImpl(final Date date, final String taskName,
			final String formatedDate, final double hours) {
		this.date = date;
		this.taskName = taskName;
		this.formatedDate = formatedDate;
		this.hours = hours;
	}

	@Override
	public Date date() {
		return this.date;
	}

	@Override
	public String taskName() {
		return this.taskName;
	}

	@Override
	public Double hours() {
		return this.hours;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(hours);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((taskName == null) ? 0 : taskName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SummaryItemImpl other = (SummaryItemImpl) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (Double.doubleToLongBits(hours) != Double
				.doubleToLongBits(other.hours)) {
			return false;
		}
		if (taskName == null) {
			if (other.taskName != null) {
				return false;
			}
		} else if (!taskName.equals(other.taskName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String formatedDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
		return "Task: " + taskName + " Date: " + formatedDate + " hours: " + hours;
	}

	@Override
	public int compareTo(final SummaryItem otherObj) {
		if (otherObj == null) {
			return 1;
		}
		final SummaryItem otherItem = otherObj;

		final int dateResult = date().compareTo(otherItem.date());
		if (dateResult != 0) {
			return dateResult;
		}

		final int taskResult = taskName().compareTo(otherItem.taskName());
		return taskResult;
	}

	@Override
	public String getFormatedDate() {
		return formatedDate;
	}
}
