import DashboardLayout from "@/components/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { trpc } from "@/lib/trpc";
import { Plus, Search, AlertCircle, Package } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const medicamentoSchema = z.object({
  nome: z.string().min(1, "Nome obrigatório"),
  descricao: z.string().optional(),
  categoriaId: z.number().default(1),
  preco: z.number().positive("Preço deve ser positivo"),
  estoque: z.number().int().nonnegative("Estoque não pode ser negativo"),
  estoqueMinimo: z.number().int().nonnegative().default(10),
  controlado: z.boolean().default(false),
});

type MedicamentoForm = z.infer<typeof medicamentoSchema>;

export default function Catalogo() {
  const [searchTerm, setSearchTerm] = useState("");
  const [openDialog, setOpenDialog] = useState(false);

  const medicamentosQuery = trpc.catalogo.getMedicamentos.useQuery();
  const createMedicamento = trpc.catalogo.createMedicamento.useMutation({
    onSuccess: () => {
      toast.success("Medicamento criado com sucesso!");
      medicamentosQuery.refetch();
      setOpenDialog(false);
      form.reset();
    },
    onError: (error) => {
      toast.error(`Erro: ${error.message}`);
    },
  });

  const form = useForm({
    resolver: zodResolver(medicamentoSchema),
    defaultValues: {
      nome: "",
      descricao: "",
      categoriaId: 1,
      preco: 0,
      estoque: 0,
      estoqueMinimo: 10,
      controlado: false,
    },
  });

  const onSubmit = (data: any) => {
    createMedicamento.mutate(data as MedicamentoForm);
  };

  const medicamentos = medicamentosQuery.data || [];
  const filteredMedicamentos = medicamentos.filter(m =>
    m.nome.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const medicamentosComBaixoEstoque = medicamentos.filter(m => m.estoque <= m.estoqueMinimo);

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Catálogo de Medicamentos</h1>
            <p className="text-slate-600 mt-2">Gestão completa de produtos e estoque</p>
          </div>
          <Dialog open={openDialog} onOpenChange={setOpenDialog}>
            <DialogTrigger asChild>
              <Button className="bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700">
                <Plus className="w-4 h-4 mr-2" />
                Novo Medicamento
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md">
              <DialogHeader>
                <DialogTitle>Adicionar Medicamento</DialogTitle>
                <DialogDescription>Preencha os dados do novo medicamento</DialogDescription>
              </DialogHeader>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <FormField
                    control={form.control}
                    name="nome"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Nome do Medicamento</FormLabel>
                        <FormControl>
                          <Input placeholder="Ex: Dipirona 500mg" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="preco"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Preço (R$)</FormLabel>
                        <FormControl>
                          <Input type="number" step="0.01" placeholder="0.00" {...field} onChange={e => field.onChange(parseFloat(e.target.value))} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="estoque"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Estoque</FormLabel>
                        <FormControl>
                          <Input type="number" placeholder="0" {...field} onChange={e => field.onChange(parseInt(e.target.value) || 0)} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="controlado"
                    render={({ field }) => (
                      <FormItem className="flex items-center gap-2">
                        <FormControl>
                          <input 
                            type="checkbox" 
                            checked={field.value} 
                            onChange={(e) => field.onChange(e.target.checked)}
                            onBlur={field.onBlur}
                            className="w-4 h-4" 
                          />
                        </FormControl>
                        <FormLabel className="mb-0">Medicamento Controlado</FormLabel>
                      </FormItem>
                    )}
                  />
                  <Button type="submit" className="w-full" disabled={createMedicamento.isPending}>
                    {createMedicamento.isPending ? "Criando..." : "Criar Medicamento"}
                  </Button>
                </form>
              </Form>
            </DialogContent>
          </Dialog>
        </div>

        {medicamentosComBaixoEstoque.length > 0 && (
          <Card className="border-orange-200 bg-orange-50">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium flex items-center gap-2 text-orange-900">
                <AlertCircle className="w-4 h-4" />
                Alerta de Estoque Baixo
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-orange-800">
                {medicamentosComBaixoEstoque.length} medicamento(s) com estoque abaixo do mínimo
              </p>
            </CardContent>
          </Card>
        )}

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle>Medicamentos Cadastrados</CardTitle>
            <CardDescription>Total: {filteredMedicamentos.length} medicamentos</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="mb-4">
              <div className="relative">
                <Search className="absolute left-3 top-3 w-4 h-4 text-slate-400" />
                <Input
                  placeholder="Buscar medicamento..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {medicamentosQuery.isLoading ? (
              <div className="text-center py-8">
                <p className="text-slate-500">Carregando medicamentos...</p>
              </div>
            ) : filteredMedicamentos.length === 0 ? (
              <div className="text-center py-12">
                <Package className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                <p className="text-slate-500">Nenhum medicamento encontrado</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredMedicamentos.map((med) => (
                  <Card key={med.id} className="border-slate-200 hover:shadow-md transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <CardTitle className="text-base">{med.nome}</CardTitle>
                          <CardDescription className="text-xs">{med.descricao}</CardDescription>
                        </div>
                        {med.controlado && (
                          <Badge variant="destructive" className="ml-2">Controlado</Badge>
                        )}
                      </div>
                    </CardHeader>
                    <CardContent className="space-y-3">
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div>
                          <p className="text-slate-500">Preço</p>
                          <p className="font-semibold text-slate-900">R$ {med.preco.toFixed(2)}</p>
                        </div>
                        <div>
                          <p className="text-slate-500">Estoque</p>
                          <p className={`font-semibold ${med.estoque <= med.estoqueMinimo ? 'text-orange-600' : 'text-emerald-600'}`}>
                            {med.estoque} un.
                          </p>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
