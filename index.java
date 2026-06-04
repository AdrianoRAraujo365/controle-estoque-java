import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// Classe para representar o Produto
class Produto {
    private String nome;
    private int quantidade;
    private double preco;

    public Produto(String nome, int quantidade, double preco) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    public double getPreco() { return preco; }

    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    // Converte o produto em uma linha de texto para salvar no arquivo
    public String toLinhaTexto() {
        return nome + ";" + quantidade + ";" + preco;
    }

    // Cria um produto a partir de uma linha de texto lida do arquivo
    public static Produto deLinhaTexto(String linha) {
        String[] partes = linha.split(";");
        String nome = partes[0];
        int quantidade = Integer.parseInt(partes[1]);
        double preco = Double.parseDouble(partes[2]);
        return new Produto(nome, quantity, preco);
    }

    @Override
    public String toString() {
        return String.format("Produto: %-20s | Qtd: %-5d | Preço: R$ %-8.2f | Total: R$ %-8.2f", 
                nome, quantidade, preco, (quantidade * preco));
    }
}

// Classe Principal do Sistema
public class SistemaEstoque {
    private static final String ARQUIVO_DADOS = "estoque.txt";
    private static ArrayList<Produto> estoque = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final int LIMITE_ESTOQUE_BAIXO = 5; // Define o que é estoque baixo

    public static void main(String[] args) {
        carregarDadosDoArquivo();
        int opcao;

        do {
            exibirMenu();
            System.out.print("Escolha uma opção: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Por favor, digite um número válido.");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (opcao) {
                case 1: cadastrarProduto(); break;
                case 2: consultarProdutos(); break;
                case 3: calcularValorTotal(); break;
                case 4: verificarEstoqueBaixo(); break;
                case 0: 
                    salvarDadosNoArquivo();
                    System.out.println("\nSistema encerrado. Dados salvos com sucesso!"); 
                    break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println("\n=================================");
        System.out.println("   CONTROLE DE ESTOQUE - LOJA   ");
        System.out.println("=================================");
        System.out.println("1. Cadastrar/Atualizar Produto");
        System.out.println("2. Consultar Produtos Cadastrados");
        System.out.println("3. Calcular Valor Total do Estoque");
        System.out.println("4. Identificar Produtos com Estoque Baixo");
        System.out.println("0. Sair e Salvar");
        System.out.println("=================================");
    }

    private static void cadastrarProduto() {
        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine().trim();

        if (nome.isEmpty()) {
            System.out.println("O nome do produto não pode ser vazio.");
            return;
        }

        System.out.print("Digite a quantidade em estoque: ");
        int quantidade = scanner.nextInt();
        System.out.print("Digite o preço unitário (Ex: 10,50): ");
        double preco = scanner.nextDouble();

        // Verifica se o produto já existe para atualizar a quantidade
        for (Produto p : estoque) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                p.setQuantidade(p.getQuantidade() + quantidade);
                System.out.println("Produto já existente! Quantidade atualizada.");
                return;
            }
        }

        estoque.add(new Produto(nome, quantidade, preco));
        System.out.println("Produto cadastrado com sucesso!");
    }

    private static void consultarProdutos() {
        if (estoque.isEmpty()) {
            System.out.println("O estoque está vazio.");
            return;
        }
        System.out.println("\n--- PRODUTOS EM ESTOQUE ---");
        for (Produto p : estoque) {
            System.out.println(p);
        }
    }

    private static void calcularValorTotal() {
        double totalGeral = 0;
        for (Produto p : estoque) {
            totalGeral += (p.getQuantidade() * p.getPreco());
        }
        System.out.printf("\n> Valor Total Patrimonial em Estoque: R$ %.2f\n", totalGeral);
    }

    private static void verificarEstoqueBaixo() {
        boolean encontrou = false;
        System.out.println("\n--- ALERTA: ESTOQUE BAIXO (Menos de " + LIMITE_ESTOQUE_BAIXO + " unidades) ---");
        for (Produto p : estoque) {
            if (p.getQuantidade() < LIMITE_ESTOQUE_BAIXO) {
                System.out.printf("Aviso: O produto '%s' tem apenas %d unidades restando.\n", p.getNome(), p.getQuantidade());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Todos os produtos estão com níveis de estoque saudáveis.");
        }
    }

    // --- MANIPULAÇÃO DE ARQUIVOS (TXT) ---

    private static void salvarDadosNoArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_DADOS))) {
            for (Produto p : estoque) {
                writer.write(p.toLinhaTexto());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados no arquivo: " + e.getMessage());
        }
    }

    private static void carregarDadosDoArquivo() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) {
            return; // Se o arquivo não existe, apenas inicia o sistema vazio
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DADOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    estoque.add(Produto.deLinhaTexto(linha));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Aviso: Houve um problema ao carregar os dados antigos. Iniciando novo estoque.");
        }
    }
}
