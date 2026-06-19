import DashboardLayout from "@/components/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ShoppingCart, Plus, Trash2, FileText } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { trpc } from "@/lib/trpc";

export default function Vendas() {
  const [tipoVenda, setTipoVenda] = useState<"balcao" | "online">("balcao");
  const [carrinho, setCarrinho] = useState<any[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [clienteId, setClienteId] = useState<number | undefined>();
  const [vendedorId, setVendedorId] = useState<number | undefined>();

  const medicamentosQuery = trpc.catalogo.getMedicamentos.useQuery();
  const createVenda = trpc.vendas.createVenda.useMutation({
    onSuccess: (result) => {
      toast.success("Venda criada com sucesso!");
      setCarrinho([]);
      setOpenDialog(false);
    },
    onError: (error) => {
      toast.error(`Erro: ${error.message}`);
    },
  });

  const adicionarAoCarrinho = (medicamento: any) => {
    if (medicamento.estoque <= 0) {
      toast.error("Produto sem estoque");
      return;
    }
    const itemExistente = carrinho.find(item => item.id === medicamento.id);
    if (itemExistente) {
      if (itemExistente.quantidade < medicamento.estoque) {
        setCarrinho(carrinho.map(item =>
          item.id === medicamento.id
            ? { ...item, quantidade: item.quantidade + 1 }
            : item
        ));
        toast.success("Quantidade aumentada");
      } else {
        toast.error("Estoque insuficiente");
      }
    } else {
      setCarrinho([...carrinho, { ...medicamento, quantidade: 1 }]);
      toast.success(`${medicamento.nome} adicionado ao carrinho`);
    }
  };

  const removerDoCarrinho = (id: number) => {
    setCarrinho(carrinho.filter(item => item.id !== id));
  };

  const atualizarQuantidade = (id: number, quantidade: number) => {
    if (quantidade <= 0) {
      removerDoCarrinho(id);
    } else {
      setCarrinho(carrinho.map(item =>
        item.id === id ? { ...item, quantidade } : item
      ));
    }
  };

  const subtotal = carrinho.reduce((sum, item) => sum + (item.preco * item.quantidade), 0);
  const desconto = 0;
  const total = subtotal - desconto;

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Módulo de Vendas</h1>
            <p className="text-slate-600 mt-2">Registre vendas no balcão ou online</p>
          </div>
          <div className="flex gap-2">
            <Button
              variant={tipoVenda === "balcao" ? "default" : "outline"}
              onClick={() => setTipoVenda("balcao")}
              className={tipoVenda === "balcao" ? "bg-gradient-to-r from-blue-500 to-cyan-600" : ""}
            >
              Balcão
            </Button>
            <Button
              variant={tipoVenda === "online" ? "default" : "outline"}
              onClick={() => setTipoVenda("online")}
              className={tipoVenda === "online" ? "bg-gradient-to-r from-blue-500 to-cyan-600" : ""}
            >
              Online
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Catálogo de Medicamentos */}
          <div className="lg:col-span-2">
            <Card className="border-0 shadow-sm">
              <CardHeader>
                <CardTitle>Catálogo de Medicamentos</CardTitle>
                <CardDescription>Selecione produtos para adicionar ao carrinho</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {medicamentosQuery.isLoading ? (
                    <div className="text-center py-8 text-slate-500">
                      <p>Carregando medicamentos...</p>
                    </div>
                  ) : (medicamentosQuery.data || []).length === 0 ? (
                    <div className="text-center py-8 text-slate-500">
                      <ShoppingCart className="w-12 h-12 mx-auto mb-3 text-slate-300" />
                      <p>Nenhum medicamento cadastrado</p>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      {(medicamentosQuery.data || []).map((med: any) => (
                        <Card key={med.id} className="border-slate-200 cursor-pointer hover:shadow-md transition-shadow">
                          <CardContent className="pt-4">
                            <div className="flex items-start justify-between mb-3">
                              <div className="flex-1">
                                <h3 className="font-semibold text-slate-900 text-sm">{med.nome}</h3>
                                <p className="text-xs text-slate-500">{med.descricao}</p>
                              </div>
                              {med.controlado && (
                                <Badge variant="destructive" className="ml-2 text-xs">Controlado</Badge>
                              )}
                            </div>
                            <div className="flex items-center justify-between">
                              <div>
                                <p className="text-xs text-slate-500">Preço</p>
                                <p className="font-bold text-slate-900">R$ {med.preco.toFixed(2)}</p>
                              </div>
                              <Button
                                size="sm"
                                onClick={() => adicionarAoCarrinho(med)}
                                disabled={med.estoque <= 0}
                                className="bg-gradient-to-r from-blue-500 to-cyan-600"
                              >
                                <Plus className="w-4 h-4" />
                              </Button>
                            </div>
                            <p className={`text-xs mt-2 ${med.estoque <= med.estoqueMinimo ? 'text-orange-600' : 'text-emerald-600'}`}>
                              Estoque: {med.estoque} un.
                            </p>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Carrinho */}
          <div>
            <Card className="border-0 shadow-sm sticky top-6">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <ShoppingCart className="w-5 h-5" />
                  Carrinho
                </CardTitle>
                <CardDescription>{carrinho.length} item(ns)</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {carrinho.length === 0 ? (
                  <div className="text-center py-6 text-slate-500">
                    <p className="text-sm">Carrinho vazio</p>
                  </div>
                ) : (
                  <>
                    <div className="space-y-3 max-h-64 overflow-y-auto">
                      {carrinho.map((item) => (
                        <div key={item.id} className="border-b border-slate-200 pb-3">
                          <div className="flex items-start justify-between mb-2">
                            <div className="flex-1">
                              <p className="font-medium text-sm text-slate-900">{item.nome}</p>
                              <p className="text-xs text-slate-500">R$ {item.preco.toFixed(2)}</p>
                            </div>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => removerDoCarrinho(item.id)}
                            >
                              <Trash2 className="w-4 h-4 text-red-500" />
                            </Button>
                          </div>
                          <div className="flex items-center gap-2">
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => atualizarQuantidade(item.id, item.quantidade - 1)}
                            >
                              -
                            </Button>
                            <span className="text-sm font-medium w-8 text-center">{item.quantidade}</span>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => atualizarQuantidade(item.id, item.quantidade + 1)}
                            >
                              +
                            </Button>
                            <span className="text-sm font-semibold text-slate-900 ml-auto">
                              R$ {(item.preco * item.quantidade).toFixed(2)}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>

                    <div className="border-t border-slate-200 pt-3 space-y-2">
                      <div className="flex justify-between text-sm">
                        <span className="text-slate-600">Subtotal:</span>
                        <span className="font-medium">R$ {subtotal.toFixed(2)}</span>
                      </div>
                      {desconto > 0 && (
                        <div className="flex justify-between text-sm text-emerald-600">
                          <span>Desconto:</span>
                          <span>-R$ {desconto.toFixed(2)}</span>
                        </div>
                      )}
                      <div className="flex justify-between text-lg font-bold bg-gradient-to-r from-blue-50 to-cyan-50 p-3 rounded">
                        <span>Total:</span>
                        <span className="text-blue-600">R$ {total.toFixed(2)}</span>
                      </div>
                    </div>

                    <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                      <DialogTrigger asChild>
                        <Button className="w-full bg-gradient-to-r from-blue-500 to-cyan-600 hover:from-blue-600 hover:to-cyan-700" disabled={carrinho.length === 0}>
                          <FileText className="w-4 h-4 mr-2" />
                          Finalizar Venda
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="sm:max-w-md">
                        <DialogHeader>
                          <DialogTitle>Confirmar Venda</DialogTitle>
                          <DialogDescription>Revise os dados antes de finalizar</DialogDescription>
                        </DialogHeader>
                        <div className="space-y-4">
                          <div className="bg-slate-50 p-4 rounded-lg space-y-2">
                            <div className="flex justify-between">
                              <span className="text-slate-600">Tipo de Venda:</span>
                              <Badge>{tipoVenda === "balcao" ? "Balcão" : "Online"}</Badge>
                            </div>
                            <div className="flex justify-between">
                              <span className="text-slate-600">Itens:</span>
                              <span className="font-medium">{carrinho.length}</span>
                            </div>
                            <div className="border-t border-slate-200 pt-2 flex justify-between font-bold">
                              <span>Total:</span>
                              <span className="text-blue-600">R$ {total.toFixed(2)}</span>
                            </div>
                          </div>
                          <Button 
                            className="w-full bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700"
                            onClick={() => {
                              createVenda.mutate({
                                clienteId,
                                vendedorId,
                                tipoVenda,
                                itens: carrinho.map(item => ({
                                  medicamentoId: item.id,
                                  quantidade: item.quantidade,
                                })),
                                desconto,
                              });
                            }}
                            disabled={createVenda.isPending}
                          >
                            {createVenda.isPending ? "Processando..." : "Confirmar e Emitir NF"}
                          </Button>
                        </div>
                      </DialogContent>
                    </Dialog>
                  </>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
