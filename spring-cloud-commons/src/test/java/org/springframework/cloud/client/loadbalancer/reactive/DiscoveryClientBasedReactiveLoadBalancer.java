package org.springframework.cloud.client.loadbalancer.reactive;

import java.util.List;
import java.util.Random;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;

/**
 * @author Olga Maciaszek-Sharma
 */
class DiscoveryClientBasedReactiveLoadBalancer implements ReactiveLoadBalancer<ServiceInstance> {

	private final Random random = new Random();

	private final String serviceId;

	private final DiscoveryClient discoveryClient;

	DiscoveryClientBasedReactiveLoadBalancer(String serviceId, DiscoveryClient discoveryClient) {
		this.serviceId = serviceId;
		this.discoveryClient = discoveryClient;
	}

	@Override
	public Publisher<Response<ServiceInstance>> choose() {
		List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
		if (instances.size() == 0) {
			return Mono.just(new EmptyResponse());
		}
		int instanceIdx = this.random.nextInt(instances.size());
		return Mono.just(new DefaultResponse(instances.get(instanceIdx)));
	}

	@Override
	public Publisher<Response<ServiceInstance>> choose(Request request) {
		return choose();
	}

}