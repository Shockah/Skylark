package me.shockah.skylark.util;

import me.shockah.skylark.func.Action0;

public final class Sync {
	public static void on(Object o1, Action0 f) {
		synchronized (o1) {
			f.call();
		}
	}
	
	public static void on(Object o1, Object o2, Action0 f) {
		synchronized (o1) {
			synchronized (o2) {
				f.call();
			}
		}
	}
	
	public static void on(Object o1, Object o2, Object o3, Action0 f) {
		synchronized (o1) {
			synchronized (o2) {
				synchronized (o3) {
					f.call();
				}
			}
		}
	}
	
	public static void on(Object o1, Object o2, Object o3, Object o4, Action0 f) {
		synchronized (o1) {
			synchronized (o2) {
				synchronized (o3) {
					synchronized (o4) {
						f.call();
					}
				}
			}
		}
	}
	
	public static void on(Object o1, Object o2, Object o3, Object o4, Object o5, Action0 f) {
		synchronized (o1) {
			synchronized (o2) {
				synchronized (o3) {
					synchronized (o4) {
						synchronized (o5) {
							f.call();
						}
					}
				}
			}
		}
	}
	
	public static void on(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Action0 f) {
		synchronized (o1) {
			synchronized (o2) {
				synchronized (o3) {
					synchronized (o4) {
						synchronized (o5) {
							synchronized (o6) {
								f.call();
							}
						}
					}
				}
			}
		}
	}
	
	private Sync() {
		throw new UnsupportedOperationException();
	}
}