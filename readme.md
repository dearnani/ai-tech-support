# 1. Navigate to project directory
cd ai-tech-support-system

# 2. Start Docker stack
docker-compose up -d

# 3. Wait for services (30 seconds)
sleep 30

# 4. Set environment variable
export OPENAI_API_KEY=sk-your-actual-key

# 5. Run Spring Boot
mvn clean install
mvn spring-boot:run

# 6. Test in another terminal
curl -X POST http://localhost:8080/api/support \
-H "Content-Type: application/json" \
-d '{"ticket": "My Spring Boot application fails to start with port already in use error"}'

# 7. View Langfuse traces
open http://localhost:3000


# Check Qdrant
curl http://localhost:6333/collections

# Check Langfuse
curl http://localhost:3000/health

# Check Kafka
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check application
curl http://localhost:8080/actuator/health
