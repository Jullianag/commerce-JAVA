# Commerce JAVA 
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/Jullianag/commerce-JAVA/blob/main/LICENSE) ![GitHub language count](https://img.shields.io/github/languages/count/Jullianag/commerce-JAVA) ![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Jullianag/commerce-JAVA) ![GitHub last commit](https://img.shields.io/github/last-commit/Jullianag/commerce-JAVA) ![GitHub repo size](https://img.shields.io/github/repo-size/Jullianag/commerce-JAVA)

## Cobertura de testes:

### Unitários
- **Jacoco**
  
[![Coverage](.github/badges/jacoco.svg)](https://github.com/Jullianag/commerce-JAVA/actions/workflows/workflow.yml)

### API TEST
- Rest Assured



# Sobre o projeto

O projeto Commerce é uma aplicação back end desenvolvida no curso **DevSuperior** do professor Nélio Alves. 
Site: [DevSuperior](https://devsuperior.com "Site da DevSuperior"). 
Porém foram acrescentadas novas funcionalidades como: cobertura de testes unitários, envio de email para o usuário recuperar a senha e
novos endpoints de usuários.

O projeto consiste em todas as estruturas de um CRUD, organizado em camadas, exceções, validações, consultas SQL e JPQL, controle de acesso e login. Nele o usuário faz autenticação, busca seus produtos, 
pedidos, categorias e ainda pode consultar suas informações de cadastro no sistema . Na área administrativa o usário pode inserir, deletar ou atualizar produtos.

Todas as requisições foram testadas via **Postman** (GET, POST, DEL, PUT).

## Algumas imagens do que seria o Layout web
![Demonstração 1](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Catalog.png)

![Demonstração 2](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Product%20Listing.png)

![Demonstração 3](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Product%20Form%20(1).png)

## Modelo conceitual
![Modelo Conceitual](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Captura%20de%20tela%202024-04-02%20155018.png)

## Postman
![Demonstração 4](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Captura%20de%20tela%202024-04-02%20164402.png)

![Demonstração 5](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Captura%20de%20tela%202024-04-02%20164439.png)

![Demonstração 6](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/Captura%20de%20tela%202024-04-02%20165024.png)

## Collection Postman
[commerce.json](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/commerce.postman_collection.json)

### Environment Postman
[environment.json](https://github.com/Jullianag/commerce-JAVA/blob/main/assets/auth%20commerce.postman_environment.json)


# Tecnologias utilizadas
## Back end
- Java
- Spring Boot
- JPA / Hibernate
- Maven

## Banco de dados:
- Banco de dados: teste H2

# Como executar o projeto

```bash
# clonar repositório
git clone https://github.com/Jullianag/commerce-JAVA

# entrar na pasta do projeto back end
cd backend

# executar o projeto
./mvnw spring-boot:run
```

## Back end
Pré-requisitos: Java 21

# Autor

Julliana Gnecco

https://www.linkedin.com/in/julliana-gnecco/
