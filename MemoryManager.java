import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Classe que representa um bloco de memoria alocado por um processo
class MemoryBlock {
    String processId; // ID do processo
    int start; // Posicao de inicio do bloco de memoria
    int size; // Tamanho do bloco de memoria

    // Construtor da classe MemoryBlock
    public MemoryBlock(String processId, int start, int size) {
        this.processId = processId;
        this.start = start;
        this.size = size;
    }
}

// Classe principal para gerenciar a memoria
public class MemoryManager {
    private int totalSize; // Tamanho total da memoria
    private List<MemoryBlock> memory; // Lista para armazenar os blocos de memoria alocados

    // Construtor que inicializa o tamanho da memoria e a lista de blocos
    public MemoryManager(int totalSize) {
        this.totalSize = totalSize;
        this.memory = new ArrayList<>();
    }

    // Metodo de alocacao usando First-Fit
    public boolean allocateFirstFit(String processId, int size) {
        // Percorre a memoria procurando o primeiro bloco livre que comporte o tamanho
        for (int i = 0; i <= totalSize - size; i++) {
            if (isFree(i, size)) { // Verifica se o bloco esta livre
                memory.add(new MemoryBlock(processId, i, size)); // Aloca o bloco
                System.out.println("First-Fit: Allocated process " + processId + " at position " + i);
                return true;
            }
        }
        System.out.println("First-Fit: ESPACO INSUFICIENTE DE MEMORIA para o processo " + processId);
        return false;
    }

    // Metodo de alocacao usando Best-Fit
    public boolean allocateBestFit(String processId, int size) {
        int bestStart = -1; // Melhor posicao de inicio encontrada
        int minWaste = Integer.MAX_VALUE; // Menor quantidade de espaco desperdicado

        // Percorre a memoria procurando o melhor bloco que acomode o tamanho
        for (int i = 0; i <= totalSize - size; i++) {
            if (isFree(i, size)) {
                int waste = calculateWaste(i, size);
                if (waste < minWaste) {
                    minWaste = waste;
                    bestStart = i;
                }
            }
        }

        if (bestStart != -1) { // Se encontrou um bloco adequado
            memory.add(new MemoryBlock(processId, bestStart, size));
            System.out.println("Best-Fit: Allocated process " + processId + " at position " + bestStart);
            return true;
        }

        System.out.println("Best-Fit: ESPACO INSUFICIENTE DE MEMORIA para o processo " + processId);
        return false;
    }

    // Metodo de alocacao usando Worst-Fit
    public boolean allocateWorstFit(String processId, int size) {
        int worstStart = -1; // Pior posicao encontrada (maior espaco livre)
        int maxWaste = -1; // Maior quantidade de espaco livre/desperdicado

        // Percorre a memoria procurando o maior bloco livre
        for (int i = 0; i <= totalSize - size; i++) {
            if (isFree(i, size)) {
                int waste = calculateWaste(i, size);
                if (waste > maxWaste) {
                    maxWaste = waste;
                    worstStart = i;
                }
            }
        }

        if (worstStart != -1) { // Se encontrou um bloco adequado
            memory.add(new MemoryBlock(processId, worstStart, size));
            System.out.println("Worst-Fit: Allocated process " + processId + " at position " + worstStart);
            return true;
        }

        System.out.println("Worst-Fit: ESPACO INSUFICIENTE DE MEMORIA para o processo " + processId);
        return false;
    }

    // Metodo de alocacao usando Circular-Fit
    public boolean allocateCircularFit(String processId, int size, int lastPosition) {
        // Primeiro tenta alocar a partir da ultima posicao usada
        for (int i = lastPosition; i < totalSize; i++) {
            if (isFree(i, size)) {
                memory.add(new MemoryBlock(processId, i, size));
                System.out.println("Circular-Fit: Allocated process " + processId + " at position " + i);
                return true;
            }
        }

        // Se nao conseguiu, volta ao inicio da memoria
        for (int i = 0; i < lastPosition; i++) {
            if (isFree(i, size)) {
                memory.add(new MemoryBlock(processId, i, size));
                System.out.println("Circular-Fit: Allocated process " + processId + " at position " + i);
                return true;
            }
        }

        System.out.println("Circular-Fit: ESPACO INSUFICIENTE DE MEMORIA para o processo " + processId);
        return false;
    }

    // Metodo para liberar o bloco de memoria de um processo
    public void release(String processId) {
        memory.removeIf(block -> block.processId.equals(processId));
        System.out.println("Released process " + processId);
    }

    // Verifica se um bloco de memoria esta livre para alocacao
    private boolean isFree(int start, int size) {
        for (MemoryBlock block : memory) {
            if (block.start < start + size && start < block.start + block.size) {
                return false;
            }
        }
        return true;
    }

    // Calcula o espaco desperdicado apos o bloco especificado
    private int calculateWaste(int start, int size) {
        int nextOccupiedStart = totalSize;
        for (MemoryBlock block : memory) {
            if (block.start >= start + size && block.start < nextOccupiedStart) {
                nextOccupiedStart = block.start;
            }
        }
        return nextOccupiedStart - (start + size);
    }

    // Mostra o estado da memoria com blocos livres e alocados representados entre
    // colchetes
    public void drawMemory() {
        StringBuilder memoryRepresentation = new StringBuilder();

        for (int i = 0; i < totalSize; i++) {
            memoryRepresentation.append("[");

            // Verifica se há um bloco alocado nessa posição
            boolean allocated = false;
            for (MemoryBlock block : memory) {
                if (i >= block.start && i < block.start + block.size) {
                    memoryRepresentation.append(block.processId.charAt(0)); // ID do processo
                    allocated = true;
                    break;
                }
            }

            if (!allocated) {
                memoryRepresentation.append(" "); // Espaço livre
            }

            memoryRepresentation.append("]");
        }

        System.out.println("Estado da Memoria:");
        System.out.println(memoryRepresentation.toString());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Entrada do tamanho da memoria
        System.out.print("Informe o tamanho da memoria: ");
        int memorySize = scanner.nextInt();
        MemoryManager manager = new MemoryManager(memorySize);

        // Seleciona o algoritmo de alocacao
        System.out.print("Selecione o algoritmo (1-First-Fit, 2-Best-Fit, 3-Worst-Fit, 4-Circular-Fit): ");
        int algorithmChoice = scanner.nextInt();
        int lastPosition = 0;

        while (true) {
            // Menu de opcoes para alocar, liberar ou mostrar memoria
            System.out.print("\n1. Alocar\n2. Liberar\n3. Mostrar Memoria\n4. Sair\nEscolha uma opcao: ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                // Entrada do ID e tamanho do processo a ser alocado
                System.out.print("ID do processo: ");
                String processId = scanner.next();
                System.out.print("Tamanho do processo: ");
                int processSize = scanner.nextInt();

                // Escolhe o metodo de alocacao com base na selecao do usuario
                switch (algorithmChoice) {
                    case 1:
                        manager.allocateFirstFit(processId, processSize);
                        break;
                    case 2:
                        manager.allocateBestFit(processId, processSize);
                        break;
                    case 3:
                        manager.allocateWorstFit(processId, processSize);
                        break;
                    case 4:
                        if (!manager.allocateCircularFit(processId, processSize, lastPosition)) {
                            lastPosition = 0; // reinicia se nao alocou
                        }
                        lastPosition = (lastPosition + processSize) % memorySize;
                        break;
                    default:
                        System.out.println("Opcao de algoritmo invalida.");
                }
                manager.drawMemory();
            } else if (choice == 2) {
                // Libera o bloco de memoria de um processo pelo ID
                System.out.print("ID do processo para liberar: ");
                String processId = scanner.next();
                manager.release(processId);
                manager.drawMemory();
            } else if (choice == 3) {
                // Exibe o estado atual da memoria
                manager.drawMemory();
            } else if (choice == 4) {
                System.out.println("Encerrando o programa.");
                break;
            } else {
                System.out.println("Opcao invalida.");
            }
        }

        scanner.close();
    }
}
