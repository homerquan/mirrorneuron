package io.convospot.engine.util

import com.redis.{RedisClientPool, RedisClient}


private[convospot] object RedisConnector {
  private val redisHost = "localhost"
  private val redisPort = 6379
  private val clientPool = new RedisClientPool(redisHost, redisPort)
  // use separate connections for each actor
  def getRedis () = {
    new RedisClient(redisHost, redisPort)
  }
  def getPool () = clientPool

}