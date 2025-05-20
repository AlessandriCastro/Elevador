

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
	//private No head, tail;
	private NoPrior headPrior, tailPrior;

	public boolean enqueue(int valor, int prior) {
		//NoPrior atualPrior = new NoPrior(prior);

		if (this.headPrior == null) {
			return false;
		} else {
			NoPrior atualPrior = this.headPrior;
			while (atualPrior != null) {
				if (atualPrior.prioridade == prior) {
					break;
				}
				atualPrior = atualPrior.proxPrior;
			}
			if (atualPrior == null) {
				return false;
			} else {
				No e = new No(valor);

				if (atualPrior.tail == null) {
					atualPrior.head = e;
					atualPrior.tail = e;
				} else {
					atualPrior.tail.prox = e;
					atualPrior.tail = e;
				}
				return true;
			}
		}

	}

	public boolean addPrioridade(int prioridade) {
		NoPrior novaPrior = new NoPrior(prioridade);

		if (prioridade < 0) {
			return false;
		} else {
			if (this.headPrior == null) {
				this.headPrior = novaPrior;
				this.tailPrior = novaPrior;
			} else {
				NoPrior atual = headPrior;
				while (atual.prioridade < prioridade && atual.proxPrior != null) {
					atual = atual.proxPrior;
				}
				if (atual.prioridade == prioridade) {
					return false;
				} else {
					novaPrior.antPrior = atual.antPrior;
					novaPrior.proxPrior = atual;
					atual.antPrior = novaPrior;
					atual.antPrior.proxPrior = novaPrior;
					return true;
				}
			}
			return true;
		}
	}
}