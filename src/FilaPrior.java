import java.io.Serializable;

class No {
	int valor;
	No prox;
	
	public No (int valor) {
		this.valor = valor;
		this.prox = null;
	}
	
	public int getValor() {
		return valor;
	}
	
}

class NoPrior{
	int prioridade;
	NoPrior antPrior, proxPrior;
	No head, tail;

	public NoPrior(int prioridade) {
		this.prioridade = prioridade;
		this.antPrior = null;
		this.proxPrior = null;
		this.head = null;
		this.tail = null;
	}	
}

class FilaPrior {
	private NoPrior headPrior, tailPrior;
	private int tamanho;

	public FilaPrior() {
		this.headPrior = null;
		this.tailPrior = null;
		this.tamanho = 0;
	}

	public boolean enqueue(int valor, int prior) {
		if (prior < 0) {
			return false;
		}

		// Primeiro, verifica se a prioridade existe
		NoPrior atualPrior = this.headPrior;
		while (atualPrior != null) {
			if (atualPrior.prioridade == prior) {
				break;
			}
			atualPrior = atualPrior.proxPrior;
		}

		// Se a prioridade não existe, cria uma nova
		if (atualPrior == null) {
			if (!addPrioridade(prior)) {
				return false;
			}
			atualPrior = this.headPrior;
			while (atualPrior != null) {
				if (atualPrior.prioridade == prior) {
					break;
				}
				atualPrior = atualPrior.proxPrior;
			}
		}

		// Adiciona o elemento na fila da prioridade
		No novoNo = new No(valor);
		if (atualPrior.tail == null) {
			atualPrior.head = novoNo;
			atualPrior.tail = novoNo;
		} else {
			atualPrior.tail.prox = novoNo;
			atualPrior.tail = novoNo;
		}
		tamanho++;
		return true;
	}

	public boolean addPrioridade(int prioridade) {
		if (prioridade < 0) {
			return false;
		}

		// Verifica se a prioridade já existe
		NoPrior atual = headPrior;
		while (atual != null) {
			if (atual.prioridade == prioridade) {
				return false;
			}
			atual = atual.proxPrior;
		}

		NoPrior novaPrior = new NoPrior(prioridade);

		// Se a lista está vazia
		if (this.headPrior == null) {
			this.headPrior = novaPrior;
			this.tailPrior = novaPrior;
			return true;
		}

		// Encontra a posição correta para inserir
		atual = headPrior;
		while (atual != null && atual.prioridade < prioridade) {
			atual = atual.proxPrior;
		}

		// Insere no início
		if (atual == headPrior) {
			novaPrior.proxPrior = headPrior;
			headPrior.antPrior = novaPrior;
			headPrior = novaPrior;
		}
		// Insere no final
		else if (atual == null) {
			novaPrior.antPrior = tailPrior;
			tailPrior.proxPrior = novaPrior;
			tailPrior = novaPrior;
		}
		// Insere no meio
		else {
			novaPrior.antPrior = atual.antPrior;
			novaPrior.proxPrior = atual;
			atual.antPrior.proxPrior = novaPrior;
			atual.antPrior = novaPrior;
		}
		return true;
	}

	public int tamanho() {
		return tamanho;
	}

	public boolean temElementosNaPrioridade(int prioridade) {
		NoPrior atual = headPrior;
		while (atual != null) {
			if (atual.prioridade == prioridade) {
				return atual.head != null;
			}
			atual = atual.proxPrior;
		}
		return false;
	}

	public int dequeue(int prioridade) {
		NoPrior atual = headPrior;
		while (atual != null) {
			if (atual.prioridade == prioridade) {
				if (atual.head == null) {
					throw new RuntimeException("Fila vazia para esta prioridade.");
				}
				
				int valor = atual.head.getValor();
				atual.head = atual.head.prox;
				
				if (atual.head == null) {
					atual.tail = null;
				}
				
				tamanho--;
				return valor;
			}
			atual = atual.proxPrior;
		}
		throw new RuntimeException("Prioridade não encontrada.");
	}

	public boolean removerPorId(int id) {
		boolean removido = false;
		NoPrior atualPrior = headPrior;
		while (atualPrior != null) {
			No anterior = null;
			No atual = atualPrior.head;
			while (atual != null) {
				if (atual.getValor() == id) {
					if (anterior == null) {
						atualPrior.head = atual.prox;
					} else {
						anterior.prox = atual.prox;
					}
					if (atualPrior.tail == atual) {
						atualPrior.tail = anterior;
					}
					tamanho--;
					removido = true;
					break; // Remove apenas a primeira ocorrência em cada prioridade
				}
				anterior = atual;
				atual = atual.prox;
			}
			atualPrior = atualPrior.proxPrior;
		}
		return removido;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		NoPrior atualPrior = headPrior;
		while (atualPrior != null) {
			sb.append("Prioridade ").append(atualPrior.prioridade).append(": [");
			No atual = atualPrior.head;
			while (atual != null) {
				sb.append(atual.getValor());
				if (atual.prox != null) {
					sb.append(", ");
				}
				atual = atual.prox;
			}
			sb.append("]");
			if (atualPrior.proxPrior != null) {
				sb.append(", ");
			}
			atualPrior = atualPrior.proxPrior;
		}
		return sb.toString();
	}
}