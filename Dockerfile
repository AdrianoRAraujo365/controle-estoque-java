# Estágio 1: Compilação (Build)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos do código fonte para o container
COPY . .

# Baixa o driver JDBC do PostgreSQL necessário para conectar ao Neon.tech
# (O comando baixa a versão estável mais recente do driver)
RUN wget https://jdbc.postgresql.org/download/postgresql-42.7.3.jar -O postgresql.jar

# Compila o código Java incluindo o driver do Postgres no classpath
RUN javac -cp postgresql.jar SistemaEstoque.java

# Estágio 2: Execução (Runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o que foi compilado do estágio anterior (deixa o container leve)
COPY --from=build /app/SistemaEstoque*.class .
COPY --from=build /app/postgresql.jar .

# Expõe a porta que o Render vai utilizar para verificar a saúde do app
EXPOSE 8080

# Comando para rodar a aplicação unindo o seu código e o Driver do Postgres
CMD ["java", "-cp", ".:postgresql.jar", "SistemaEstoque"]
