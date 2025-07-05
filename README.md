# Projeto Napster

Esse repositório contém código do projeto baseado no Napster da disciplina "Sistemas Distribuídos" do IFPB - Campus Cajazeiras. Os requisitos necessários para a execução desse projeto são:

- JDK versão 17. Download [aqui](https://www.oracle.com/br/java/technologies/downloads/#jdk17) ou use um gerenciador de versões como [SDKMAN!](https://sdkman.io/install) (Recomendado!). Ou, no linux, digite no terminal:
```bash
sudo apt install openjdk17-jdk maven -y
```

# Executando

Ao fazer o clone do repositório, abra o terminal no diretório do projeto e execute os comandos linha por linha.

O sistema foi criado para rodar em computadores diferentes, sejam físicos ou virtuais. Então:

No computador que será o servidor, entre na pasta ```/server``` e digite:

```bash
mvn clean package
mvn exec:java -D exec.mainClass="br.mikaelstl.filesystem.App"
```

No computador que será o cliente, faça a mesma coisa, porém na pasta ```/client```, ao iniciar você poderá digitar os comandos, para se comunicar com o servidor:

```bash
JOIN <seu_ip>

CREATEFILE <seu_ip> <nome_do_arquivo> <tamanho>

SEARCH <nome_do_arquivo>

LEAVE <seu_ip>

GET <ip_de_onde_quer_baixar> <arquivo>
```

Para sair basta digitar o comando LEAVE e seu ip.