import 'package:flutter/material.dart';
import 'package:santa_ana_dashboard/core/theme/app_theme.dart';
import 'package:santa_ana_dashboard/core/widgets/shimmer_placeholder.dart';

class LoadingView extends StatelessWidget {
  const LoadingView({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppTheme.bgColor,
      child: SingleChildScrollView(
        physics: const NeverScrollableScrollPhysics(),
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 18, 16, 20),
          child: Column(
            children: [

              // HEADER
              Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: const [
                        ShimmerPlaceholder(
                          width: 140,
                          height: 12,
                          borderRadius: 4,
                        ),
                        SizedBox(height: 10),
                        ShimmerPlaceholder(
                          width: 220,
                          height: 24,
                          borderRadius: 6,
                        ),
                      ],
                    ),
                  ),

                  const ShimmerPlaceholder(
                    width: 42,
                    height: 42,
                    borderRadius: 21,
                  ),
                ],
              ),

              const SizedBox(height: 24),

              // FECHA
              const Align(
                alignment: Alignment.centerLeft,
                child: ShimmerPlaceholder(
                  width: 180,
                  height: 10,
                  borderRadius: 4,
                ),
              ),

              const SizedBox(height: 18),

              // TARJETA PRINCIPAL
              Container(
                height: 220,
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: AppTheme.cardColor,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: AppTheme.borderColor,
                  ),
                ),
                child: const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    ShimmerPlaceholder(
                      width: 120,
                      height: 12,
                    ),

                    SizedBox(height: 20),

                    ShimmerPlaceholder(
                      width: 110,
                      height: 60,
                    ),

                    SizedBox(height: 15),

                    ShimmerPlaceholder(
                      width: 180,
                      height: 10,
                    ),

                    Spacer(),

                    ShimmerPlaceholder(
                      width: double.infinity,
                      height: 8,
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 12),

              // DOS TARJETAS
              Row(
                children: [

                  Expanded(
                    child: Container(
                      height: 180,
                      padding: const EdgeInsets.all(18),
                      decoration: BoxDecoration(
                        color: AppTheme.cardColor,
                        borderRadius: BorderRadius.circular(16),
                        border: Border.all(
                          color: AppTheme.borderColor,
                        ),
                      ),
                      child: const Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          ShimmerPlaceholder(
                            width: 90,
                            height: 12,
                          ),

                          SizedBox(height: 18),

                          ShimmerPlaceholder(
                            width: 80,
                            height: 28,
                          ),

                          SizedBox(height: 12),

                          ShimmerPlaceholder(
                            width: 100,
                            height: 10,
                          ),

                          Spacer(),

                          ShimmerPlaceholder(
                            width: double.infinity,
                            height: 40,
                          ),
                        ],
                      ),
                    ),
                  ),

                  const SizedBox(width: 10),

                  Expanded(
                    child: Container(
                      height: 180,
                      padding: const EdgeInsets.all(18),
                      decoration: BoxDecoration(
                        color: AppTheme.cardColor,
                        borderRadius: BorderRadius.circular(16),
                        border: Border.all(
                          color: AppTheme.borderColor,
                        ),
                      ),
                      child: const Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          ShimmerPlaceholder(
                            width: 90,
                            height: 12,
                          ),

                          SizedBox(height: 18),

                          ShimmerPlaceholder(
                            width: 70,
                            height: 28,
                          ),

                          SizedBox(height: 18),

                          ShimmerPlaceholder(
                            width: double.infinity,
                            height: 8,
                          ),

                          SizedBox(height: 12),

                          ShimmerPlaceholder(
                            width: double.infinity,
                            height: 8,
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 20),

              // TITULO RESERVAS
              const Align(
                alignment: Alignment.centerLeft,
                child: ShimmerPlaceholder(
                  width: 150,
                  height: 14,
                ),
              ),

              const SizedBox(height: 12),

              // RESERVAS
              ...List.generate(
                3,
                (index) => Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: AppTheme.cardColor,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: AppTheme.borderColor,
                      ),
                    ),
                    child: const Row(
                      children: [

                        ShimmerPlaceholder(
                          width: 40,
                          height: 40,
                          borderRadius: 20,
                        ),

                        SizedBox(width: 12),

                        Expanded(
                          child: Column(
                            crossAxisAlignment:
                                CrossAxisAlignment.start,
                            children: [
                              ShimmerPlaceholder(
                                width: 120,
                                height: 12,
                              ),

                              SizedBox(height: 8),

                              ShimmerPlaceholder(
                                width: 170,
                                height: 10,
                              ),
                            ],
                          ),
                        ),

                        ShimmerPlaceholder(
                          width: 35,
                          height: 20,
                          borderRadius: 8,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}