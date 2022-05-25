# doddle

Doddle is a *background job system* designed to make scheduling jobs in Java *as simple as possible* without being tied to a particular framework or storage provider.  It's a doddle!

> It is currently a WIP, it is not stable or ready to be used in any production setting.

# Quick overfiew

Out-the-box, Doddle comes with the following features:

 - Enqueue jobs immediately or in the future
 - Queues with a given priority
 - Scheduled jobs, otherwise knows as, CRONs
 - Encryption of job arguments and key rotation
 - Retry and back-off strategies (e.g. jitter, linear etc.)
 - REST API to fetch information about jobs
 - Storage (only PostgreSQL is supported for the moment) and java framework agnostic
 - Middleware to modify the execution pipeline of a job
 - Circuit breaker to protect the underlying storage layer
 - Telemetry (listen to jobs events such as job created, executing, failed etc.)
 - Job progress and logging
 - Web dashboard (in progress!)

Example:

```java
@Task()
public void processOrders(final ExecutionContext context) {
    final var tenantId = context.argument("tenantId").asString();
    final var orders = orderService.getOrdersForTenantId(tenantId);
    final var progress = context.progress(orders.size());
    // let's process some orders!
    for (final var order: orders) {
	orderService.process(order);
	progress.advance();
        context.logger.info("Currently processed {}% of orders", progress.percentage());
    }
    context.logger.info("Finished processing orders");
}
```


# Getting started

Documentation can be found [here](https://jamhall.gitbook.io/doddle/).
