/*
 * Copyright 2017 jiajunhui
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.kk.taurus.http_helper.thread;

import com.kk.taurus.threadpool.ThreadPoolExecutorConstructor;

public class ThreadManager {

	private static ThreadManager instance;
	private ThreadPoolExecutorConstructor poolExecutorConstructor;
	private static final int CORE_THREAD_COUNT = 5;
	private static final int MAX_THREAD_COUNT = 10;
	private static final long KEEP_ALIVE_TIME_MILLIS = 30L;

	private ThreadManager(){
		poolExecutorConstructor = new ThreadPoolExecutorConstructor(CORE_THREAD_COUNT,MAX_THREAD_COUNT,KEEP_ALIVE_TIME_MILLIS);
	}

	public static ThreadManager getInstance(){
		if(null == instance){
			synchronized (ThreadManager.class){
				if(null == instance){
					instance = new ThreadManager();
				}
			}
		}
		return instance;
	}

	public void execute(Runnable runnable){
		poolExecutorConstructor.execute(runnable);
	}
}
