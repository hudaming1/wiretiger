package org.hum.wiretiger.core.ssl;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

import org.hum.wiretiger.common.exception.WireTigerException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hudaming
 */
@Slf4j
public class CA_Station {

	private static final ConcurrentHashMap<String, FutureTask<byte[]>> CERT_CACHE = new ConcurrentHashMap<>();

	public static ByteArrayInputStream createWithCache(String domain) {
		FutureTask<byte[]> futureTask = CERT_CACHE.get(domain);
		if (futureTask == null) {
			futureTask = new FutureTask<byte[]>(new CA_Creator(domain));
			FutureTask<byte[]> task = CERT_CACHE.putIfAbsent(domain, futureTask);
			if (task == null) {
				task = futureTask;
				task.run();
			}
		}
		try {
			return new ByteArrayInputStream(futureTask.get());
		} catch (Exception e) {
			CERT_CACHE.remove(domain);
			log.error("create cert error, domain=" + domain, e);
			throw new WireTigerException("create cert error, domain=" + domain, e);
		}
	}
}
